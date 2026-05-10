package com.abhiumale.kartikmartapp.ui.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abhiumale.kartikmartapp.R
import kotlinx.coroutines.delay

@Composable
fun BannerSection() {

    val bannerImgList = listOf(
        R.drawable.a_s_img1,
        R.drawable.a_s_img2,
        R.drawable.a_s_img5,
        R.drawable.a_s_img4,
    )

    val pagerState = rememberPagerState(
        pageCount = { bannerImgList.size }
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage =
                (pagerState.currentPage + 1) % bannerImgList.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column {

        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) { page ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Image(
                    painter = painterResource(bannerImgList[page]),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            repeat(bannerImgList.size) { index ->

                val selected =
                    pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected)
                                Color(0xFF0F8F5F)
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                )
            }
        }
    }
}