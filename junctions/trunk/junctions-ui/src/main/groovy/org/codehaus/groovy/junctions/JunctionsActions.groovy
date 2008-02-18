/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.junctions

action(id: 'exitAction',
        name: 'Exit',
        closure: controller.&exit,
        mnemonic: 'X'
        )

action(id: 'aboutAction',
        name: 'About',
        closure: controller.&showAbout
        )

action(id: 'refreshSubscriptionsAction',
        name: 'Refresh Subscriptions',
        closure: controller.&refreshSubscriptions,
        //mnemonic: 'R',
        //accelerator: shortcut('R'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/reload_16.png")),
        shortDescription: 'Refresh subscriptions',
        //enabled: false
        )

action(id: 'refreshSubscriptionAction',
        name: 'Refresh Subscription',
        closure: controller.&refreshSubscription,
        mnemonic: 'R',
        accelerator: shortcut('R'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("org/tango-project/tango-icon-theme/16x16/actions/view-refresh.png")),
        shortDescription: 'Refresh subscription',
        enabled: true
        )

action(id: 'addSubscriptionAction',
        name: 'Add Subscription',
        closure: controller.&showAddSubscription,
        mnemonic: 'A',
        accelerator: shortcut('A'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/subscribe_16.png")),
        shortDescription: 'Add a subscription'
        )

action(id: 'manageSubscriptionsAction',
        name: 'Manage Subscriptions',
        closure: controller.&manageSubscriptions,
        mnemonic: 'M',
        accelerator: shortcut('M'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/info_16.png")),
        shortDescription: 'Manage your subscriptions',
        enabled: false
        )

action(id: 'nextSubscriptionAction',
        name: 'Next Subscription',
        closure: controller.&nextPost,
        mnemonic: 'E',
        accelerator: shortcut('E'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/next_rss_16.png")),
        shortDescription: 'Next subscription',
        enabled: false
        )

action(id: 'previousSubscriptionAction',
        name: 'Previous Subscription',
        closure: controller.&previousPost,
        mnemonic: 'V',
        accelerator: shortcut('V'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/previous_rss_16.png")),
        shortDescription: 'Previous subscription',
        enabled: false
        )

action(id: 'nextPostAction',
        name: 'Next Post',
        closure: controller.&nextPost,
        mnemonic: 'N',
        accelerator: shortcut('N'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/next_unread_16.png")),
        shortDescription: 'Next post',
        enabled: false
        )

action(id: 'previousPostAction',
        name: 'Previous Post',
        closure: controller.&previousPost,
        mnemonic: 'P',
        accelerator: shortcut('P'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/previous_unread_16.png")),
        shortDescription: 'Previous post',
        enabled: false
        )

action(id: 'markAllAsReadAction',
        name: 'Mark all as Read',
        closure: controller.&markAllAsRead,
        mnemonic: 'K',
        accelerator: shortcut('K'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("zeusboxstudio-feedicons2/mark_as_read_16.png")),
        shortDescription: 'Mark all as read',
        enabled: false
        )

action(id: 'markAsFavoriteAction',
        name: 'Mark as Favorite',
        closure: controller.&markAsFavorite,
        mnemonic: 'F',
        accelerator: shortcut('F'),
        smallIcon: imageIcon(image: ViewUtils.loadImage("org/tango-project/tango-icon-theme/16x16/emblems/emblem-favorite.png")),
        shortDescription: 'Mark as favorite',
        enabled: false
        )

action(id: 'cosmosStatsAction',
        name: 'Technorati',
        closure: {evt -> controller.subscriptionStatsFrom(evt, 'Technorati')},
        smallIcon: imageIcon(resource: "icons/technorati-icon.png", class: this),
        shortDescription: 'Stats from Technorati',
        //enabled: false
        )

action(id: 'diggStatsAction',
        name: 'digg',
        closure: {evt -> controller.subscriptionStatsFrom(evt, 'digg')},
        smallIcon: imageIcon(resource: "icons/digg-icon.png", class: this),
        shortDescription: 'Stats from digg',
        enabled: false
        )

action(id: 'dzoneStatsAction',
        name: 'DZone',
        closure: {evt -> controller.subscriptionStatsFrom(evt, 'DZone')},
        smallIcon: imageIcon(resource: "icons/dzone-icon.png", class: this),
        shortDescription: 'Stats from DZone',
        enabled: false
        )

action(id: 'deliciousBookmarkAction',
        name: 'del.icio.us',
        closure: {evt -> controller.bookmarkTo(evt, 'delicious')},
        smallIcon: imageIcon(resource: "icons/delicious-icon.gif", class: this),
        shortDescription: 'Bookmark to del.icio.us',
        enabled: false
        )

action(id: 'diggBookmarkAction',
        name: 'digg',
        closure: {evt -> controller.bookmarkTo(evt, 'diggs')},
        smallIcon: imageIcon(resource: "icons/digg-icon.png", class: this),
        shortDescription: 'Bookmark to digg',
        enabled: false
        )

action(id: 'preferencesAction',
        name: 'Preferences',
        closure: controller.&showPreferences,
        smallIcon: imageIcon(image: ViewUtils.loadImage("org/tango-project/tango-icon-theme/16x16/actions/document-properties.png")),
        shortDescription: 'Preferences'
        )
