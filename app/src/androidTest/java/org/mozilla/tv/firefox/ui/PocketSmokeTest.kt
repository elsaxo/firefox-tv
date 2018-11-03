/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.ui

import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mozilla.FirefoxTestApplication
import org.mozilla.tv.firefox.helpers.AndroidAssetDispatcher
import org.mozilla.tv.firefox.helpers.MainActivityTestRule
import org.mozilla.tv.firefox.helpers.TestAssetHelper
import org.mozilla.tv.firefox.pocket.PocketVideoRepo
import org.mozilla.tv.firefox.pocket.PocketViewModel
import org.mozilla.tv.firefox.ui.robots.navigationOverlay

class PocketSmokeTest {

    @get:Rule val activityTestRule = MainActivityTestRule()

    private lateinit var app: FirefoxTestApplication
    private lateinit var page: TestAssetHelper.TestAsset

    @Before
    fun setup() {
        app = activityTestRule.activity.application as FirefoxTestApplication

        val server = MockWebServer().apply {
            setDispatcher(AndroidAssetDispatcher())
            start()
        }

        page = TestAssetHelper.getGenericAssets(server).first()

        val mockedState = PocketVideoRepo.FeedState.LoadComplete(listOf(
            PocketViewModel.FeedItem.Video(
                id = 0,
                title = "Title",
                url = page.urlStr,
                thumbnailURL = "https://blog.mozilla.org/firefox/files/2017/12/Screen-Shot-2017-12-18-at-2.39.25-PM.png",
                popularitySortId = 0
            )
        ))

        app.pushPocketRepoState(mockedState)
    }

    @Test
    fun verify_that_pocket_happy_path_loads_expected_url() {
        navigationOverlay {}
        .openPocketMegatile {}
        .openTileToBrowser(0) {
            assertTestContent(page.content)
        }
    }
}
