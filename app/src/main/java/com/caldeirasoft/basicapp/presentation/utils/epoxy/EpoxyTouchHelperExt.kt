package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.*
import java.util.*

/**
 * A simple way to set up drag or swipe interactions with Epoxy.
 *
 *
 * Drag events work with the EpoxyController and automatically update the controller and
 * RecyclerView when an item is moved. You just need to implement a callback to update your data to
 * reflect the change.
 *
 *
 * Both swipe and drag events implement a small lifecycle to help you style the views as they are
 * moved. You can register callbacks for the lifecycle events you care about.
 *
 *
 * If you want to set up multiple drag and swipe rules for the same RecyclerView, you can use this
 * class multiple times to specify different targets or swipe and drag directions and callbacks.
 *
 *
 * If you want more control over configuration and handling, you can opt to not use this class and
 * instead you can implement [EpoxyModelTouchCallback] directly with your own [ ]. That class provides an interface that makes it easier to work with Epoxy models
 * and simplifies touch callbacks.
 *
 *
 * If you want even more control you can implement [EpoxyTouchHelperCallback]. This is just a
 * light layer over the normal RecyclerView touch callbacks, but it converts all view holders to
 * Epoxy view holders to remove some boilerplate for you.
 */
object EpoxyTouchHelperExt {

    /**
     * The entry point for setting up drag support.
     *
     * @param controller The EpoxyController with the models that will be dragged. The controller will
     * be updated for you when a model is dragged and moved by a user's touch
     * interaction.
     */
    fun initDragging(controller: EpoxyController): DragBuilder {
        return DragBuilder(controller)
    }

    class DragBuilder constructor(private val controller: EpoxyController) {

        /**
         * The recyclerview that the EpoxyController has its adapter added to. An [ ] will be created and configured for you, and
         * attached to this RecyclerView.
         */
        fun withRecyclerView(recyclerView: RecyclerView): DragBuilder2 {
            return DragBuilder2(controller, recyclerView)
        }
    }

    class DragBuilder2 constructor(private val controller: EpoxyController, private val recyclerView: RecyclerView) {

        /** Enable dragging vertically, up and down.  */
        fun forVerticalList(): DragBuilder3 {
            return withDirections(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
        }

        /** Enable dragging horizontally, left and right.  */
        fun forHorizontalList(): DragBuilder3 {
            return withDirections(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        }

        /** Enable dragging in all directions.  */
        fun forGrid(): DragBuilder3 {
            return withDirections(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT
                    or ItemTouchHelper.RIGHT)
        }

        /**
         * Set custom movement flags to dictate which drag directions should be allowed.
         *
         *
         * Can be any of [ItemTouchHelper.LEFT], [ItemTouchHelper.RIGHT], [ ][ItemTouchHelper.UP], [ItemTouchHelper.DOWN], [ItemTouchHelper.START], [ ][ItemTouchHelper.END]
         *
         *
         * Flags can be OR'd together to allow multiple directions.
         */
        fun withDirections(directionFlags: Int): DragBuilder3 {
            return DragBuilder3(controller, recyclerView, makeMovementFlags(directionFlags, 0))
        }
    }

    class DragBuilder3 constructor(private val controller: EpoxyController, private val recyclerView: RecyclerView, private val movementFlags: Int) {

        /**
         * Set the type of Epoxy model that is draggable. This approach works well if you only have one
         * draggable type.
         */
        fun <U : EpoxyModel<*>> withTarget(targetModelClass: Class<U>): DragBuilder4<U> {
            val targetClasses = ArrayList<Class<out EpoxyModel<*>>>(1)
            targetClasses.add(targetModelClass)

            return DragBuilder4(controller, recyclerView, movementFlags, targetModelClass,
                    targetClasses)
        }

        /**
         * Specify which Epoxy model types are draggable. Use this if you have more than one type that
         * is draggable.
         *
         *
         * If you only have one draggable type you should use [.withTarget]
         */
        fun withTargets(vararg targetModelClasses: Class<out EpoxyModel<*>>): DragBuilder4<EpoxyModel<*>> {
            return DragBuilder4(controller, recyclerView, movementFlags, EpoxyModel::class.java,
                    Arrays.asList<Class<out EpoxyModel<*>>>(*targetModelClasses))
        }

        /**
         * Use this if all models in the controller should be draggable, and if there are multiple types
         * of models in the controller.
         *
         *
         * If you only have one model type you should use [.withTarget]
         */
        fun forAllModels(): DragBuilder4<EpoxyModel<*>> {
            return withTarget(EpoxyModel::class.java)
        }
    }

    class DragBuilder4<U : EpoxyModel<*>> constructor(private val controller: EpoxyController,
                                                              private val recyclerView: RecyclerView, private val movementFlags: Int,
                                                              private val targetModelClass: Class<U>, private val targetModelClasses: List<Class<out EpoxyModel<*>>>) {

        /**
         * Set callbacks to handle drag actions and lifecycle events.
         *
         *
         * You MUST implement [DragCallbacks.onModelMoved] to update your data to reflect an item move.
         *
         *
         * You can optionally implement the other callbacks to modify the view being dragged. This is
         * useful if you want to change things like the view background, size, color, etc
         *
         * @return An [ItemTouchHelper] instance that has been initialized and attached to a
         * recyclerview. The touch helper has already been fully set up and can be ignored, but you may
         * want to hold a reference to it if you need to later detach the recyclerview to disable touch
         * events via setting null on [ItemTouchHelper.attachToRecyclerView]
         */
        fun andCallbacks(callbacks: DragCallbacks<U>): ItemTouchHelper {
            val itemTouchHelper = ItemTouchHelper(object : EpoxyModelTouchCallback<U>(controller, targetModelClass) {

                override fun getMovementFlagsForModel(model: U, adapterPosition: Int): Int {
                    return movementFlags
                }

                override fun isTouchableModel(model: EpoxyModel<*>): Boolean {
                    val isTargetType = if (targetModelClasses.size == 1)
                        super.isTouchableModel(model)
                    else
                        targetModelClasses.contains(model.javaClass)


                    return isTargetType && callbacks.isDragEnabledForModel(model as U)
                }

                override fun onDragStarted(model: U?, itemView: View?, adapterPosition: Int) {
                    callbacks.onDragStarted(model, itemView, adapterPosition)
                }

                override fun onDragReleased(model: U?, itemView: View?) {
                    callbacks.onDragReleased(model, itemView)
                }

                override fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: U?,
                                          itemView: View?) {
                    callbacks.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView)
                }

                override fun clearView(model: U?, itemView: View?) {
                    callbacks.clearView(model, itemView)
                }
            })

            itemTouchHelper.attachToRecyclerView(recyclerView)

            return itemTouchHelper
        }
    }

    abstract class DragCallbacks<T : EpoxyModel<*>> : EpoxyDragCallback<T> {

        override fun onDragStarted(model: T?, itemView: View?, adapterPosition: Int) {

        }

        override fun onDragReleased(model: T?, itemView: View?) {

        }

        abstract override fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: T?,
                                           itemView: View?)

        override fun clearView(model: T?, itemView: View?) {

        }

        /**
         * Whether the given model should be draggable.
         *
         *
         * True by default. You may override this to toggle draggability for a model.
         */
        fun isDragEnabledForModel(model: T): Boolean {
            return true
        }

        override fun getMovementFlagsForModel(model: T, adapterPosition: Int): Int {
            // No-Op this is not used
            return 0
        }
    }

    /**
     * The entry point for setting up swipe support for a RecyclerView. The RecyclerView must be set
     * with an Epoxy adapter or controller.
     */
    fun initSwiping(recyclerView: RecyclerView): SwipeBuilder {
        return SwipeBuilder(recyclerView)
    }

    class SwipeBuilder constructor(private val recyclerView: RecyclerView) {

        /** Enable swiping right.  */
        fun right(): SwipeBuilder2 {
            return withDirections(ItemTouchHelper.RIGHT)
        }

        /** Enable swiping left.  */
        fun left(): SwipeBuilder2 {
            return withDirections(ItemTouchHelper.LEFT)
        }

        /** Enable swiping horizontally, left and right.  */
        fun leftAndRight(): SwipeBuilder2 {
            return withDirections(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        }

        /**
         * Set custom movement flags to dictate which swipe directions should be allowed.
         *
         *
         * Can be any of [ItemTouchHelper.LEFT], [ItemTouchHelper.RIGHT], [ ][ItemTouchHelper.UP], [ItemTouchHelper.DOWN], [ItemTouchHelper.START], [ ][ItemTouchHelper.END]
         *
         *
         * Flags can be OR'd together to allow multiple directions.
         */
        fun withDirections(directionFlags: Int): SwipeBuilder2 {
            return SwipeBuilder2(recyclerView, makeMovementFlags(0, directionFlags))
        }
    }

    class SwipeBuilder2 constructor(private val recyclerView: RecyclerView,
                                            private val movementFlags: Int) {

        /**
         * Set the type of Epoxy model that is swipable. Use this if you only have one
         * swipable type.
         */
        fun <U : EpoxyModel<*>> withTarget(targetModelClass: Class<U>): SwipeBuilder3<U> {
            val targetClasses = ArrayList<Class<out EpoxyModel<*>>>(1)
            targetClasses.add(targetModelClass)

            return SwipeBuilder3(recyclerView, movementFlags, targetModelClass,
                    targetClasses)
        }

        /**
         * Specify which Epoxy model types are swipable. Use this if you have more than one type that
         * is swipable.
         *
         *
         * If you only have one swipable type you should use [.withTarget]
         */
        fun withTargets(
                vararg targetModelClasses: Class<out EpoxyModel<*>>): SwipeBuilder3<EpoxyModel<*>> {
            return SwipeBuilder3(recyclerView, movementFlags, EpoxyModel::class.java,
                    Arrays.asList<Class<out EpoxyModel<*>>>(*targetModelClasses))
        }

        /**
         * Use this if all models in the controller should be swipable, and if there are multiple types
         * of models in the controller.
         *
         *
         * If you only have one model type you should use [.withTarget]
         */
        fun forAllModels(): SwipeBuilder3<EpoxyModel<*>> {
            return withTarget(EpoxyModel::class.java)
        }
    }

    class SwipeBuilder3<U : EpoxyModel<*>> constructor(
            private val recyclerView: RecyclerView, private val movementFlags: Int,
            private val targetModelClass: Class<U>, private val targetModelClasses: List<Class<out EpoxyModel<*>>>) {

        /**
         * Set callbacks to handle swipe actions and lifecycle events.
         *
         *
         * You MUST implement [SwipeCallbacks.onSwipeCompleted] to
         * remove the swiped item from your data and request a model build.
         *
         *
         * You can optionally implement the other callbacks to modify the view as it is being swiped.
         *
         * @return An [ItemTouchHelper] instance that has been initialized and attached to a
         * recyclerview. The touch helper has already been fully set up and can be ignored, but you may
         * want to hold a reference to it if you need to later detach the recyclerview to disable touch
         * events via setting null on [ItemTouchHelper.attachToRecyclerView]
         */
        fun andCallbacks(callbacks: SwipeCallbacksExt<U>): ItemTouchHelper {
            val itemTouchHelper = ItemTouchHelper(object : EpoxyModelTouchCallback<U>(null, targetModelClass) {

                override fun getMovementFlagsForModel(model: U, adapterPosition: Int): Int {
                    return movementFlags
                }

                override fun isTouchableModel(model: EpoxyModel<*>): Boolean {
                    val isTargetType = if (targetModelClasses.size == 1)
                        super.isTouchableModel(model)
                    else
                        targetModelClasses.contains(model.javaClass)


                    return isTargetType && callbacks.isSwipeEnabledForModel(model as U)
                }

                override fun onSwipeStarted(model: U, itemView: View, adapterPosition: Int) {
                    callbacks.onSwipeStarted(model, itemView, adapterPosition)
                }

                override fun onSwipeProgressChanged(model: U, itemView: View, swipeProgress: Float,
                                                    canvas: Canvas) {
                    callbacks.onSwipeProgressChanged(model, itemView, swipeProgress, canvas)
                }

                override fun onSwipeCompleted(model: U, itemView: View, position: Int, direction: Int) {
                    callbacks.onSwipeCompleted(model, itemView, position, direction)
                }

                override fun onSwipeReleased(model: U, itemView: View) {
                    callbacks.onSwipeReleased(model, itemView)
                }

                override fun clearView(model: U, itemView: View) {
                    callbacks.clearView(model, itemView)
                }

                override fun getSwipeThreshold(viewHolder: EpoxyViewHolder?): Float {
                    return callbacks.getSwipeThreshold()
                }

                override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                    return callbacks.getSwipeEscapeVelocity()
                }
            })

            itemTouchHelper.attachToRecyclerView(recyclerView)

            return itemTouchHelper
        }
    }

    abstract class SwipeCallbacksExt<T : EpoxyModel<*>> : EpoxySwipeCallback<T> {

        companion object {
            const val DEFAULT_THRESHOLD:Float = 0.5f
            const val DEFAULT_SWIPE_ESCAPE_VELOCITY = 240f
        }

        override fun onSwipeStarted(model: T, itemView: View, adapterPosition: Int) { }

        override fun onSwipeProgressChanged(model: T, itemView: View, swipeProgress: Float,
                                            canvas: Canvas) { }

        abstract override fun onSwipeCompleted(model: T, itemView: View, position: Int, direction: Int)

        override fun onSwipeReleased(model: T, itemView: View) { }

        override fun clearView(model: T, itemView: View) { }

        /**
         * Whether the given model should be swipable.
         *
         *
         * True by default. You may override this to toggle swipabaility for a model.
         */
        fun isSwipeEnabledForModel(model: T): Boolean { return true }

        override fun getMovementFlagsForModel(model: T, adapterPosition: Int): Int {
            // Not used
            return 0
        }

        open fun getSwipeThreshold(): Float = DEFAULT_THRESHOLD

        open fun getSwipeEscapeVelocity(): Float = DEFAULT_SWIPE_ESCAPE_VELOCITY

    }
}
