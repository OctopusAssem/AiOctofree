package com.octopusassem.aioctofree.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onAddKey: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
        ) {
            Text(
                "مفتوح المصدر  ·  3 مزوّدين مجانيين",
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 7.dp),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(236.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.34f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                Color.Transparent,
                            ),
                        ),
                        CircleShape,
                    ),
            )
            Image(
                painter = painterResource(com.octopusassem.aioctofree.R.drawable.brand_logo),
                contentDescription = "شعار AiOctofree",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(222.dp),
            )
        }

        Text(
            "AiOctofree",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5f).sp,
        )
        Text(
            "كل أدوات AI المجانية، في محطة واحدة",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            "اسأل، صنّف سؤالك، واختار الموديل الأنسب. محادثاتك محفوظة على موبايلك.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            FeatureLine("محادثات مع Groq وGemini وOpenRouter")
            FeatureLine("أرشيف داخلي ومفاتيح مشفّرة على جهازك")
            FeatureLine("مرشد يرشّح أنسب AI لنوع السؤال")
        }

        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary,
                        ),
                    ),
                )
                .clickable(onClick = onStart),
            contentAlignment = Alignment.Center,
        ) {
            Text("ابدأ المحطة", color = Color(0xFF07111F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        OutlinedButton(
            onClick = onAddKey,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 9.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("إعداد مفتاح AI مجاني", fontWeight = FontWeight.SemiBold)
        }
        Text(
            "من غير حساب داخل التطبيق · من غير اشتراك مننا",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 15.dp),
        )
    }
}

@Composable
private fun FeatureLine(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.84f))
            .padding(horizontal = 13.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary),
                    ),
                    CircleShape,
                ),
        )
        Text(text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
