package com.example.customizableslider

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customizableslider.data.DataSource
import com.example.customizableslider.ui.theme.CustomizableSliderTheme
import com.example.customizableslider.ui.theme.Purple40
import com.example.customizableslider.ui.theme.Purple80
import com.example.customizableslider.ui.theme.PurpleGrey40
import com.example.customizableslider.ui.theme.PurpleGrey80
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomizableSliderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = { AppBarTop()}) {
                        Column(modifier = Modifier.padding(it)) {
                            CustomizableSliderApp()
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun CustomizableSliderApp(){
    CustomizableSliderLayout(DataSource().loadData())
}


val seconds = listOf(
    1,3,5
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomSlider(imageList: List<Int>,
                 pagerState: PagerState,
                 modifier: Modifier = Modifier){


//    val isSelected by remember {
//        mutableStateOf(false)
//    }

    Column(modifier = modifier) {
        HorizontalPager(state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
                page ->
            Image(painter = painterResource(imageList[page]),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp)))

        }
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            repeat(pagerState.pageCount){
                var isSelected = pagerState.currentPage == it
                Box(modifier = Modifier
                    .padding(4.dp)
                    .width(
                        if (isSelected) animateDpAsState(
                            20.dp,
                            label = ""
                        ).value else animateDpAsState(8.dp, label = "").value
                    )
                    .height(8.dp)
                    .background(
                        if (isSelected) Purple80 else PurpleGrey80,
                        RoundedCornerShape(12.dp)
                    )) {

                }
            }

        }
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun AppBarTop(){
    CenterAlignedTopAppBar(title = { Text(text = "Image Slider",
        color =  Color(219, 219, 219, 255),
        fontWeight = FontWeight.Medium)},
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(
            49,
            47,
            49,
            255
        )
        ))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomizableSliderLayout(imageList: List<Int>,
                             modifier: Modifier = Modifier){
    val context = LocalContext.current
    var isCheckedAutoSlide by remember {
        mutableStateOf(false)
    }
    var delay by remember {
        mutableStateOf(2)
    }
    var pagerState = rememberPagerState(pageCount = {
        imageList.size
    })
    var isError by remember {
        mutableStateOf(false)
    }
    var selectedSec by remember {
        mutableStateOf(1)
    }
    var scope = rememberCoroutineScope()
    Column(modifier = modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        ) {
        CustomSlider(imageList = imageList,
            pagerState,
            modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Auto Slide?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                Switch(checked = isCheckedAutoSlide,
                    onCheckedChange = {
                        isCheckedAutoSlide = !isCheckedAutoSlide
                        if(isCheckedAutoSlide){
                            Toast.makeText(context, "Auto Slide ON", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(context, "Auto Slide OFF", Toast.LENGTH_LONG).show()
                        }
                        scope.launch {
                            while (isCheckedAutoSlide) {
                                delay(1000)
                                delay((selectedSec-1) * 1000L)
                                if (isCheckedAutoSlide) {
                                    pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
                                }
                            }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isCheckedAutoSlide) {
                Text(text = "Delay between slides")
                Spacer(modifier = Modifier.height(4.dp))
//                TextField(
//                    value = delay.toString(), onValueChange = {
//                        delay = it.toInt()
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                )
                Column {
                    seconds.forEachIndexed { idx, ele ->
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            RadioButton(selected = selectedSec == ele,
                                onClick = { selectedSec = ele })
                            Text(text = "${ele} sec")

                        }
                    }
                }

//                Row(verticalAlignment = Alignment.CenterVertically) {
//
//                    RadioButton(selected = true, onClick = { /*TODO*/ })
//                    Text(text = "3 sec")
//
//                }
//                Row(verticalAlignment = Alignment.CenterVertically) {
//
//                    RadioButton(selected = true, onClick = { /*TODO*/ })
//                    Text(text = "5 sec")
//
//                }
//            }
                Spacer(modifier = Modifier.height(8.dp))
                if (isError) {
                    Text(
                        text = "*delay is invalid*",
                        color = Color.Red
                    )
                }

            }

        }}

}