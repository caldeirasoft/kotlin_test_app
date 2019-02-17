package com.caldeirasoft.basicapp

import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.EpisodeEntity
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.PodcastEntity

class Mockup {

    companion object {
        fun getEpisodesMockup(): List<Episode> {
            val listEpisodes = ArrayList<Episode>(20).apply {
                add(EpisodeEntity("dSYDUND//pa6c6BlCFdKeKeYPdvJNkaaU76nUOW4fbQ=_1673571721c:565590c:d42b93be",
                        "http://www.2hdp.fr/2HDP.xml",
                        "Tree of life",
                        1542794400000).apply {
                    podcastTitle = "2 Heures De Perdues"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/f5/eb/17/f5eb1782-c59a-f372-042d-f0d6cc4ce11b/source/100x100bb.jpg"
                    description = ""
                    mediaUrl = "http://dts.podtrac.com/redirect.mp3/www.2hdp.fr/episodes/s5/5-9-Tree-Of-Life.mp3"
                })
                add(EpisodeEntity("dSYDUND//pa6c6BlCFdKeKeYPdvJNkaaU76nUOW4fbQ=_1671167aedf:2710bb5:88373a59",
                        "http://www.2hdp.fr/2HDP.xml",
                        "Le transporteur",
                        1542189600000).apply {
                    podcastTitle = "2 Heures De Perdues"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/f5/eb/17/f5eb1782-c59a-f372-042d-f0d6cc4ce11b/source/100x100bb.jpg"
                    description = ""
                    mediaUrl = "http://dts.podtrac.com/redirect.mp3/www.2hdp.fr/episodes/s5/5-8-Transporteur.mp3"
                })
                add(EpisodeEntity("dSYDUND//pa6c6BlCFdKeKeYPdvJNkaaU76nUOW4fbQ=_166ed4042c8:1c689c8:75727aa2",
                        "http://www.2hdp.fr/2HDP.xml",
                        "Color of night",
                        1541584800000).apply {
                    podcastTitle = "2 Heures De Perdues"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/f5/eb/17/f5eb1782-c59a-f372-042d-f0d6cc4ce11b/source/100x100bb.jpg"
                    description = ""
                    mediaUrl = "http://dts.podtrac.com/redirect.mp3/www.2hdp.fr/episodes/s5/5-7-Color-Of-Night.mp3"
                })
                add(EpisodeEntity("LHkCuXbSrh/tc7ZyuUAHz9G202AqF1FArkYRJaDEqwY=_1675093da65:16ab48c:93a62bfd",
                        "http://www.2hdp.fr/culture/culture.xml",
                        "Les Pirates", 1543226400000
                ).apply {
                    podcastTitle = "Culture 2000"
                    imageUrl = "https://is3-ssl.mzstatic.com/image/thumb/Music118/v4/86/02/06/860206ff-1ec6-ae75-85b9-4b61efae4641/source/100x100bb.jpg"
                    description = ""
                    mediaUrl = "http://dts.podtrac.com/redirect.mp3/www.2hdp.fr/culture/episodes/s4/4-13-Pirates.mp3"
                })
                add(EpisodeEntity("LHkCuXbSrh/tc7ZyuUAHz9G202AqF1FArkYRJaDEqwY=_1675093da65:16ab48c:93a62bfd",
                        "http://www.2hdp.fr/culture/culture.xml",
                        "Le procès Eichmann", 1542621600000
                ).apply {
                    podcastTitle = "Culture 2000"
                    imageUrl = "https://is3-ssl.mzstatic.com/image/thumb/Music118/v4/86/02/06/860206ff-1ec6-ae75-85b9-4b61efae4641/source/100x100bb.jpg"
                    description = ""
                    mediaUrl = "http://dts.podtrac.com/redirect.mp3/www.2hdp.fr/culture/episodes/s4/4-12-Eichmann.mp3"
                })
                add(EpisodeEntity("LHkCuXbSrh/tc7ZyuUAHz9G202AqF1FArkYRJaDEqwY=_1675093da65:16ab48b:93a62bfd",
                        "http://www.2hdp.fr/culture/culture.xml",
                        "La Naissance de l'Etat d'Israël", 1542016800000
                ).apply {
                    podcastTitle = "Culture 2000"
                    imageUrl = "https://is3-ssl.mzstatic.com/image/thumb/Music118/v4/86/02/06/860206ff-1ec6-ae75-85b9-4b61efae4641/source/100x100bb.jpg"
                    description = ""
                    mediaUrl = "http://dts.podtrac.com/redirect.mp3/www.2hdp.fr/culture/episodes/s4/4-11-Naissance-Israel.mp3"
                })
                add(EpisodeEntity("LHkCuXbSrh/tc7ZyuUAHz9G202AqF1FArkYRJaDEqwY=_1675093da65:16ab488:93a62bfd",
                        "http://www.2hdp.fr/culture/culture.xml",
                        "La chasse aux sorcières", 1540807200000
                ).apply {
                    podcastTitle = "Culture 2000"
                    imageUrl = "https://is3-ssl.mzstatic.com/image/thumb/Music118/v4/86/02/06/860206ff-1ec6-ae75-85b9-4b61efae4641/source/100x100bb.jpg"
                    description = "Alors qu'il a déjà provoqué une interruptions de séance ce mercredi à l'Assemblée nationale en portant un gilet jaune, Jean Lassalle a décidé de totalement s'engager pour le mouvement."
                    mediaUrl = "http://dts.podtrac.com/redirect.mp3/www.2hdp.fr/culture/episodes/s4/4-9-Chasse-sorciere.mp3"
                })
                add(EpisodeEntity("UQU2rEydZ6TXU3n3rWHp9Y0UL9OW3VZ7C11JnJWSqHU=_1673afb1082:5b6ac11:88373a59",
                        "http://cdn-europe1.new2.ladmedia.fr/var/exports/podcasts/sound/revue-de-presque.xml",
                        "Jean Lassalle s'engage auprès des \\\"gilets jaunes\\\" à l'Assemblée nationale : \\\"Je suis le Jean Moulin des marcassins !\\\" (Canteloup)", 1542872520000
                ).apply {
                    podcastTitle = "Nicolas Canteloup - la revue de presque sur Europe 1"
                    imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music128/v4/2d/22/fc/2d22fc0d-8eae-63a6-8619-8e604c20b885/source/100x100bb.jpg"
                    description = ""
                    mediaUrl = "http://api.europe1.fr/podcast/mp3/itunes-289339403/3805547/podcast.mp3"
                })
                add(EpisodeEntity("UQU2rEydZ6TXU3n3rWHp9Y0UL9OW3VZ7C11JnJWSqHU=_1674f94a911:71dc10e:88373a59",
                        "http://cdn-europe1.new2.ladmedia.fr/var/exports/podcasts/sound/revue-de-presque.xml",
                        "Emmanuel Macron se lâche sur les \\\"gilets jaunes\\\" : \\\"Laissez-les manifester, le mouvement va s'essouffler ! Il faut bien que jaunisse se passe !\\\" (Canteloup)", 1543218120000
                ).apply {
                    podcastTitle = "Nicolas Canteloup - la revue de presque sur Europe 1"
                    imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music128/v4/2d/22/fc/2d22fc0d-8eae-63a6-8619-8e604c20b885/source/100x100bb.jpg"
                    description = "Alors que les &quot;gilets jaunes&quot; ont semé la violence sur les Champs Élysées ce week-end, Emmanuel Macron attend que le mouvement s'arrête de lui-même."
                    mediaUrl = "http://api.europe1.fr/podcast/mp3/itunes-289339403/3808181/podcast.mp3"
                })
                add(EpisodeEntity("UQU2rEydZ6TXU3n3rWHp9Y0UL9OW3VZ7C11JnJWSqHU=_1674021893a:64d96d1:d42b93be",
                        "http://cdn-europe1.new2.ladmedia.fr/var/exports/podcasts/sound/revue-de-presque.xml",
                        "BEST OF - C'est presque l'affaire Carlos Ghosn", 1542958920000
                ).apply {
                    podcastTitle = "Nicolas Canteloup - la revue de presque sur Europe 1"
                    imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music128/v4/2d/22/fc/2d22fc0d-8eae-63a6-8619-8e604c20b885/source/100x100bb.jpg"
                    description = "Chaque samedi, Nicolas Canteloup vous offre le meilleur de ses imitations dans la Revue de presque."
                    mediaUrl = "http://api.europe1.fr/podcast/mp3/itunes-289339403/3806456/podcast.mp3"
                })
                add(EpisodeEntity("UQU2rEydZ6TXU3n3rWHp9Y0UL9OW3VZ7C11JnJWSqHU=_1673fb3959e:62e3708:9dda030",
                        "http://cdn-europe1.new2.ladmedia.fr/var/exports/podcasts/sound/revue-de-presque.xml",
                        "Nikos Aliagas déménage avec Europe 1 : \\\"Je suis le premier matinalier d'Europe 1 à faire mes cartons sans être viré !\\\" (Canteloup)", 1542958920000
                ).apply {
                    podcastTitle = "Nicolas Canteloup - la revue de presque sur Europe 1"
                    imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music128/v4/2d/22/fc/2d22fc0d-8eae-63a6-8619-8e604c20b885/source/100x100bb.jpg"
                    description = "À l'occasion du déménagement d'Europe 1 dans le 15e arrondissement de Paris, toute l'équipe de la matinale fait ses cartons."
                    mediaUrl = "http://api.europe1.fr/podcast/mp3/itunes-289339403/3806429/podcast.mp3"
                })
                add(EpisodeEntity("MnHo2hZG0JlOw0Zx68pSx6AsXLfJAV0y2cJgSRM499c=_167501ade0b:762bd0d:d42b93be",
                        "http://www.rireetchansons.fr/rss/podcasts/feed-marceau-refait-l-info.xml",
                        "Gilets Jaunes aux Champs Elysées, Compile Coupe Davis d'adieu de Yannick Noah - Marceau refait l'info - 26 novembre 2018", 1543210200000
                ).apply {
                    podcastTitle = "Marceau refait l'info"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/c9/7c/a4/c97ca411-ce05-4fd8-3d23-a726a104b74c/source/100x100bb.jpg"
                    description = "Avec les imitations de Stéphane Plaza, Nicolas Sarkozy, Jean-Jacques Bourdin, Nelson Montfort, Alain Souchon"
                    mediaUrl = "https://spodcasts.nrjaudio.fm/podcasts/fr/3/150/3_marceau_refait_l_info_le_26_11_2018_pod_3113.mp3"
                })
                add(EpisodeEntity("MnHo2hZG0JlOw0Zx68pSx6AsXLfJAV0y2cJgSRM499c=_167410cd40f:62fded7:88373a59",
                        "http://www.rireetchansons.fr/rss/podcasts/feed-marceau-refait-l-info.xml",
                        "Finale de la Coupe Davies - Marceau refait l'info - 23 novembre 2018", 1542949200000
                ).apply {
                    podcastTitle = "Marceau refait l'info"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/c9/7c/a4/c97ca411-ce05-4fd8-3d23-a726a104b74c/source/100x100bb.jpg"
                    description = "Avec les imitations de Thierry Ardisson, Philippe Manoeuvre, Johnny Hallyday, Frédéric Mitterrand, Didier Deschamps, DSK, Patrick Sébastien"
                    mediaUrl = "https://spodcasts.nrjaudio.fm/podcasts/fr/3/150/3_marceau_refait_l_info_le_23_11_2018_pod_7896.mp3"
                })
                add(EpisodeEntity("MnHo2hZG0JlOw0Zx68pSx6AsXLfJAV0y2cJgSRM499c=_1673b761d4b:5ef88f6:d42b93be",
                        "http://www.rireetchansons.fr/rss/podcasts/feed-marceau-refait-l-info.xml",
                        "Lauryn Hill huée, Jean Lassale en gilet jaune, dons de selles à l'hôpital - Marceau refait l'info - 22 novembre 2018", 1542862800000
                ).apply {
                    podcastTitle = "Marceau refait l'info"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/c9/7c/a4/c97ca411-ce05-4fd8-3d23-a726a104b74c/source/100x100bb.jpg"
                    description = "Avec les imitations de Nikos Aliagas, Charles Aznavour, Jean Lassale, Jean-Michel Apathie, Nicolas Sarkozy, Emmanuel Macron, Patrick Sébastien."
                    mediaUrl = "https://spodcasts.nrjaudio.fm/podcasts/fr/3/150/3_marceau_refait_l_info_le_22_11_2018_pod_2645.mp3"
                })
                add(EpisodeEntity("MnHo2hZG0JlOw0Zx68pSx6AsXLfJAV0y2cJgSRM499c=_16736bab540:57644d1:9dda0303",
                        "http://www.rireetchansons.fr/rss/podcasts/feed-marceau-refait-l-info.xml",
                        "Black Friday, la chatte à Deschamps, Gainsbourg chante les Gilets Jaunes - Marceau refait l'info - 21 novembre 2018", 1542776400000
                ).apply {
                    podcastTitle = "Marceau refait l'info"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/c9/7c/a4/c97ca411-ce05-4fd8-3d23-a726a104b74c/source/100x100bb.jpg"
                    description = "Avec les imitations de Jean-Marie Le Pen, Renaud, Emmanuel Macron, François Bayrou, Didier Deschamps"
                    mediaUrl = "https://spodcasts.nrjaudio.fm/podcasts/fr/3/150/3_marceau_refait_l_info_le_21_11_2018_pod_9117.mp3"
                })
                add(EpisodeEntity("QV3vXo2zf9X2iBYw66T9wyvLGmSF9fvoTM/TGa9g/Js=_1673b631203:5d9faf4:9dda0303",
                        "http://radiofrance-podcast.net/podcast09/rss_18348.xml",
                        "Le succès de l'emoji caca...", 1542882780000
                ).apply {
                    podcastTitle = "La Drôle d'Humeur de Marina Rollman"
                    imageUrl = "https://is2-ssl.mzstatic.com/image/thumb/Music128/v4/b4/fc/77/b4fc7775-ca46-2413-93b9-0608c944254f/source/100x100bb.jpg"
                    description = "durée : 00:04:08 - La drôle d’humeur de Marina Rollman - par : Marina Rollman - Une fois n’est pas coutume je voulais aujourd’hui aborder une question un peu grave…Qui est l’interrogation suivante :quel verrou mental a du saute pour qu’en tant que société, on créé et on soit d’accord d’utiliser un emoji caca ?"
                    mediaUrl = "http://rf.proxycast.org/1505557330685599744/18348-22.11.2018-ITEMA_21900251-0.mp3"
                })
                add(EpisodeEntity("QV3vXo2zf9X2iBYw66T9wyvLGmSF9fvoTM/TGa9g/Js=_16717aaaf82:2fca6dc:88373a59",
                        "http://radiofrance-podcast.net/podcast09/rss_18348.xml",
                        "On ne meurt plus beaucoup d’amour...", 1542277980000
                ).apply {
                    podcastTitle = "La Drôle d'Humeur de Marina Rollman"
                    imageUrl = "https://is2-ssl.mzstatic.com/image/thumb/Music128/v4/b4/fc/77/b4fc7775-ca46-2413-93b9-0608c944254f/source/100x100bb.jpg"
                    description = "durée : 00:04:31 - La drôle d’humeur de Marina Rollman - par : Marina Rollman - Aujourd’hui je voulais vous parler d’amour parce que l’autre jour j’étais à l’opéra et à un moment donné j’entends ça…\\nMe infelice, s'ella accetta! Disperato io morirò"
                    mediaUrl = "http://rf.proxycast.org/1502966104262189056/18348-15.11.2018-ITEMA_21893519-0.mp3"
                })
                add(EpisodeEntity("QV3vXo2zf9X2iBYw66T9wyvLGmSF9fvoTM/TGa9g/Js=_166f38bd8e0:324186:88373a59",
                        "http://radiofrance-podcast.net/podcast09/rss_18348.xml",
                        "On peut arrêter de chercher, on connaît l’homme le plus sexy du monde", 1541673180000
                ).apply {
                    podcastTitle = "La Drôle d'Humeur de Marina Rollman"
                    imageUrl = "https://is2-ssl.mzstatic.com/image/thumb/Music128/v4/b4/fc/77/b4fc7775-ca46-2413-93b9-0608c944254f/source/100x100bb.jpg"
                    description = "durée : 00:04:41 - La drôle d’humeur de Marina Rollman - par : Marina Rollman - Et c’est George Pompidou !\\nNON C’EST PAS VRAI JE VOUS AI BIEN EUS…"
                    mediaUrl = "http://rf.proxycast.org/1500359116105719808/18348-08.11.2018-ITEMA_21886734-0.mp3"
                })
                add(EpisodeEntity("QV3vXo2zf9X2iBYw66T9wyvLGmSF9fvoTM/TGa9g/Js=_166cef14127:ad3059:d42b93be",
                        "http://radiofrance-podcast.net/podcast09/rss_18348.xml",
                        "Marina à nouveau active sur les réseaux sociaux", 1541067720000
                ).apply {
                    podcastTitle = "La Drôle d'Humeur de Marina Rollman"
                    imageUrl = "https://is2-ssl.mzstatic.com/image/thumb/Music128/v4/b4/fc/77/b4fc7775-ca46-2413-93b9-0608c944254f/source/100x100bb.jpg"
                    description = "durée : 00:03:47 - La drôle d’humeur de Marina Rollman - par : Marina Rollman - J’avais un peu décroché des réseaux sociaux ces derniers mois… Je me sentais femme et terriblement supérieure à vous tous pauvres mortels agglutinés à vos écrans comme des bœufs"
                    mediaUrl = "http://rf.proxycast.org/1497736377272377344/18348-01.11.2018-ITEMA_21877605-0.mp3"
                })
                add(EpisodeEntity("pr/btuvAQgH44/oKRxHWJdhAjYYMu4yuMm6QhlRdiqw=_167363344a3:54e8e0a:88373a59",
                        "http://radiofrance-podcast.net/podcast09/rss_16609.xml",
                        "Je suis ce que la science appelle un homme d’alcool", 1542796380000
                ).apply {
                    podcastTitle = "La Drôle d'Humeur de Guillermo Guiz"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music71/v4/35/70/b8/3570b8c2-faef-39e8-fd9a-90e86f08b4f9/source/100x100bb.jpg"
                    description = "durée : 00:03:46 - La drôle d'humeur de Guillermo Guiz - Comme vous Leila, si vous aviez ne fut-ce qu’une petite prostate, mais non, mais non, madame préfère faire cavalier seul avec ses organes de filles ! Bravo ! Super, le vivre-ensemble !"
                    mediaUrl = "http://rf.proxycast.org/1505182106257465344/16609-21.11.2018-ITEMA_21899195-0.mp3"
                })
                add(EpisodeEntity("pr/btuvAQgH44/oKRxHWJdhAjYYMu4yuMm6QhlRdiqw=_1671c65f733:377a38f:d42b93be",
                        "http://radiofrance-podcast.net/podcast09/rss_16609.xml",
                        "Une application pour calculer le nombre d'heures à regarder les séries TV", 1542367440000
                ).apply {
                    podcastTitle = "La Drôle d'Humeur de Guillermo Guiz"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music71/v4/35/70/b8/3570b8c2-faef-39e8-fd9a-90e86f08b4f9/source/100x100bb.jpg"
                    description = "durée : 00:03:24 - La drôle d'humeur de Guillermo Guiz - Hier matin, j’étais devant ma douche, j’hésitais, j’étais là : « j’y vais, j’y vais pas ? », je me disais qu’est-ce qu’il aurait fait, Daniel Morin, dans cette situation ?"
                    mediaUrl = "http://rf.proxycast.org/1503318625031823360/16609-16.11.2018-ITEMA_21894661-0.mp3"
                })
                add(EpisodeEntity("pr/btuvAQgH44/oKRxHWJdhAjYYMu4yuMm6QhlRdiqw=_166ee608af3:1eac4c2:d42b93be",
                        "http://radiofrance-podcast.net/podcast09/rss_16609.xml",
                        "La guerre, ça va, merci, je connais", 1541586780000
                ).apply {
                    podcastTitle = "La Drôle d'Humeur de Guillermo Guiz"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music71/v4/35/70/b8/3570b8c2-faef-39e8-fd9a-90e86f08b4f9/source/100x100bb.jpg"
                    description = "durée : 00:03:28 - La drôle d'humeur de Guillermo Guiz - C’est pour ça que Verdun, ça me touche, les commémorations de l’Armistice, tout ça, je m’identifie."
                    mediaUrl = "http://rf.proxycast.org/1499990179455180800/16609-07.11.2018-ITEMA_21884737-0.mp3"
                })
                add(EpisodeEntity("pr/btuvAQgH44/oKRxHWJdhAjYYMu4yuMm6QhlRdiqw=_166871350c5:af20c3:9dda0303",
                        "http://radiofrance-podcast.net/podcast09/rss_16609.xml",
                        "Beaucoup d’homophobie à Paris ce moment", 1539854520000
                ).apply {
                    podcastTitle = "La Drôle d'Humeur de Guillermo Guiz"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music71/v4/35/70/b8/3570b8c2-faef-39e8-fd9a-90e86f08b4f9/source/100x100bb.jpg"
                    description = "durée : 00:03:21 - La drôle d'humeur de Guillermo Guiz - Ca va les amigos ? Ca fait longtemps ! Bon, vous avez vu, beaucoup d’homophobie à Paris ce moment « Oui, on a vu, mais d’habitude elles viennent plus tard, non, les vannes sur Daniel Morin ?"
                    mediaUrl = "http://rf.proxycast.org/1492546882755371008/16609-18.10.2018-ITEMA_21857882-0.mp3"
                })
                add(EpisodeEntity("pr/btuvAQgH44/oKRxHWJdhAjYYMu4yuMm6QhlRdiqw=_16639a17793:315afae:19b4cc1a",
                        "http://radiofrance-podcast.net/podcast09/rss_16609.xml",
                        "Guillermo à lu le Point !", 1538558520000
                ).apply {
                    podcastTitle = "La Drôle d'Humeur de Guillermo Guiz"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music71/v4/35/70/b8/3570b8c2-faef-39e8-fd9a-90e86f08b4f9/source/100x100bb.jpg"
                    description = "durée : 00:03:20 - La drôle d'humeur de Guillermo Guiz - Et plus précisément un dossier sur les sagouins..."
                    mediaUrl = "http://rf.proxycast.org/1486983519321202688/16609-03.10.2018-ITEMA_21836626-0.mp3"
                })
            }
            return listEpisodes
        }

        fun getPodcasts(): List<Podcast> {
            val listPodcasts = ArrayList<Podcast>().apply {
                add(PodcastEntity().apply {
                    title = "Le Billet de Daniel Morin"
                    feedUrl = "http://radiofrance-podcast.net/podcast09/rss_12371.xml"
                    imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music128/v4/2d/22/fc/2d22fc0d-8eae-63a6-8619-8e604c20b885/source/400x400bb.jpg"
                    authors = "Europe 1"
                })
                add(PodcastEntity().apply {
                    title = "Culture 2000"
                    feedUrl = "http://www.2hdp.fr/culture/culture.xml"
                    imageUrl = "https://is3-ssl.mzstatic.com/image/thumb/Music118/v4/86/02/06/860206ff-1ec6-ae75-85b9-4b61efae4641/source/400x400bb.jpg"
                    authors = "Fréquence Moderne"
                })
                add(PodcastEntity().apply {
                    title = "Marceau refait l'info"
                    feedUrl = "http://www.rireetchansons.fr/rss/podcasts/feed-marceau-refait-l-info.xml"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/c9/7c/a4/c97ca411-ce05-4fd8-3d23-a726a104b74c/source/400x400bb.jpg"
                    authors = "Rire et Chansons France"
                })
                add(PodcastEntity().apply {
                    title = "L'appel trop con de Rire & Chansons"
                    feedUrl = "http://www.rireetchansons.fr/rss/podcasts/feed-l-appel-trop-con-de-rire-chansons.xml"
                    imageUrl = "https://is3-ssl.mzstatic.com/image/thumb/Music128/v4/50/3e/3b/503e3bd9-f387-504b-d04a-c0313106930d/source/400x400bb.jpg"
                    authors = "Rire et Chansons France"
                })
                add(PodcastEntity().apply {
                    title = "Nicolas Canteloup - la revue de presque sur Europe 1"
                    feedUrl = "http://cdn-europe1.new2.ladmedia.fr/var/exports/podcasts/sound/revue-de-presque.xml"
                    imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music128/v4/2d/22/fc/2d22fc0d-8eae-63a6-8619-8e604c20b885/source/400x400bb.jpg"
                    authors = "Europe 1"
                })
                add(PodcastEntity().apply {
                    title = "Super Moscato Show"
                    feedUrl = "http://www.1001podcast.com/podcast/RMCInfo/channel131/RMCInfochannel131.xml"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music62/v4/0d/c1/88/0dc188c2-a1e3-7a96-bbc8-b54be431c735/source/400x400bb.jpg"
                    authors = "RMC"
                })
                add(PodcastEntity().apply {
                    title = "La Drôle d'Humeur de Marina Rollman"
                    feedUrl = "http://radiofrance-podcast.net/podcast09/rss_18348.xml"
                    imageUrl = "https://is2-ssl.mzstatic.com/image/thumb/Music128/v4/b4/fc/77/b4fc7775-ca46-2413-93b9-0608c944254f/source/400x400bb.jpg"
                    authors = "France Inter"
                })
                add(PodcastEntity().apply {
                    title = "2 Heures De Perdues"
                    feedUrl = "http://www.2hdp.fr/2HDP.xml"
                    imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/f5/eb/17/f5eb1782-c59a-f372-042d-f0d6cc4ce11b/source/400x400bb.jpg"
                    authors = "Fréquence Moderne"
                })
                add(PodcastEntity().apply {
                    title = "Buzzer Time"
                    feedUrl = "http://podcast.rmc.fr/channel250/RMCInfochannel250.xml"
                    imageUrl = "https://is3-ssl.mzstatic.com/image/thumb/Music118/v4/67/ed/88/67ed8864-1eb3-6a1a-b3ff-8489d608088b/source/400x400bb.jpg"
                    authors = "RMC"
                })
                add(PodcastEntity().apply {
                    title = "Le moment Meurice"
                    feedUrl = "http://radiofrance-podcast.net/podcast09/rss_14257.xml"
                    imageUrl = "https://is4-ssl.mzstatic.com/image/thumb/Music62/v4/10/a4/dc/10a4dc64-16c2-274b-b534-e23fa23216b4/source/400x400bb.jpg"
                    authors = "France Inter"
                })
                add(PodcastEntity().apply {
                    title = "La Drôle d'Humeur d'Alison Wheeler"
                    feedUrl = "http://radiofrance-podcast.net/podcast09/rss_16723.xml"
                    imageUrl = "http://media.radiofrance-podcast.net/podcast09/logo_1.jpg"
                    authors = "France Inter"
                })
                add(PodcastEntity().apply {
                    title = "La Drôle d'Humeur de Frédérick Sigrist"
                    feedUrl = "http://radiofrance-podcast.net/podcast09/rss_14215.xml"
                    imageUrl = "http://media.radiofrance-podcast.net/podcast09/RF_OMM_0000013994_ITE.jpg"
                    authors = "France Inter"
                })
                add(PodcastEntity().apply {
                    title = "L'After Foot"
                    feedUrl = "http://www.1001podcast.com/podcast/RMCInfo/channel59/RMCInfochannel59.xml"
                    imageUrl = "https://frontrmcimg.streamakaci.com/images/podcasts_1400_After_Footjpg_20170927143600.jpg"
                    authors = "RMC"
                })
            }
            return listPodcasts
        }
    }
}