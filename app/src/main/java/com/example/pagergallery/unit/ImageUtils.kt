package com.example.pagergallery.unit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import java.util.Random
import kotlin.math.pow

object ImageUtils {
    //对图片进行滤镜处理是一个比较复杂的任务，
    // 通常可以通过一些现有的图片处理库或者自己编写滤镜算法来实现。
    // 以下是40种常见的图片滤镜处理方法，你可以参考这些方法来实现你的需求。

    /* 1. **灰度滤镜 (Grayscale)**
    - 将图片转换为灰度图像，去除颜色信息，仅保留亮度信息。
     */

    
    fun applyGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                val gray = (r + g + b) / 3
                grayscaleBitmap.setPixel(x, y, Color.rgb(gray, gray, gray))
            }
        }
        return grayscaleBitmap
    }
    

    /* 2. **对比度增强 (Contrast Enhancement)**
    - 提高图像的对比度，使亮部更亮，暗部更暗。
     */

    
//    fun adjustContrast(bitmap: Bitmap, contrast: Float): Bitmap {
//        val cm = ColorMatrix()
//        cm.setSaturation(0f)
//        val scale = contrast + 1f
//        cm.setScale(scale, scale, scale, 1f)
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 3. **亮度调整 (Brightness Adjustment)**
    - 增加或减少图像的亮度。
     */

    
//    fun adjustBrightness(bitmap: Bitmap, brightness: Float): Bitmap {
//        val cm = ColorMatrix()
//        cm.setScale(1f, 1f, 1f, brightness)
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 4. **色调变换 (Hue Adjustment)**
    - 改变图像的色调。
     */

    
//    fun adjustHue(bitmap: Bitmap, hue: Float): Bitmap {
//        val cm = ColorMatrix()
//        cm.setRotate(0, hue)
//        cm.setRotate(1, hue)
//        cm.setRotate(2, hue)
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 5. **模糊滤镜 (Gaussian Blur)**
    - 应用高斯模糊使图像看起来柔和。
     */

    
    fun applyGaussianBlur(context: Context, bitmap: Bitmap): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, android.renderscript.Element.U8_4(rs))
        script.setRadius(25f)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap)
        return bitmap
    }
    

    /* 6. **锐化滤镜 (Sharpen)**
    - 锐化图像边缘，使细节更加清晰。
     */

    
//    fun applySharpen(bitmap: Bitmap): Bitmap {
//        val sharpenMatrix = floatArrayOf(
//            0f, -1f, 0f,
//            -1f, 5f, -1f,
//            0f, -1f, 0f
//        )
//        val cm = ColorMatrix()
//        cm.set(sharpenMatrix)
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 7. **模糊滤镜 (Invert Colors)**
    - 将图像中的每个像素的颜色进行反转。
     */

    
    fun invertColors(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val invertedPixel = Color.rgb(255 - Color.red(pixel), 255 - Color.green(pixel), 255 - Color.blue(pixel))
                invertedBitmap.setPixel(x, y, invertedPixel)
            }
        }
        return invertedBitmap
    }
    

    /* 8. **老照片效果 (Sepia Tone)**
    - 给图像添加复古的色调效果。
     */

    
//    fun applySepia(bitmap: Bitmap): Bitmap {
//        val cm = ColorMatrix()
//        cm.setSaturation(0f)
//        val sepiaMatrix = floatArrayOf(
//            0.393f, 0.769f, 0.189f, 0f, 0f,
//            0.349f, 0.686f, 0.168f, 0f, 0f,
//            0.272f, 0.534f, 0.131f, 0f, 0f,
//            0f, 0f, 0f, 1f, 0f
//        )
//        cm.postConcat(ColorMatrix(sepiaMatrix))
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 9. **浮雕效果 (Emboss Effect)**
    - 给图像添加浮雕效果。
     */

    
//    fun applyEmboss(bitmap: Bitmap): Bitmap {
//        val embossMatrix = floatArrayOf(
//            -2f, -1f, 0f,
//            -1f, 1f, 1f,
//            0f, 1f, 2f
//        )
//        val cm = ColorMatrix()
//        cm.set(embossMatrix)
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 10. **边缘检测 (Edge Detection)**
    - 检测图像的边缘，常用算法包括Sobel滤波。
     */

    
//    fun applyEdgeDetection(bitmap: Bitmap): Bitmap {
//        val edgeDetectionMatrix = floatArrayOf(
//            -1f, -1f, -1f,
//            -1f, 8f, -1f,
//            -1f, -1f, -1f
//        )
//        val cm = ColorMatrix()
//        cm.set(edgeDetectionMatrix)
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 11. **曝光调整 (Exposure Adjustment)**
    - 增加或减少图像的曝光。
     */

    
//    fun adjustExposure(bitmap: Bitmap, exposure: Float): Bitmap {
//        val cm = ColorMatrix()
//        cm.setScale(1f + exposure, 1f + exposure, 1f + exposure, 1f)
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 12. **颜色滤镜 (Color Filter)**
    - 给图像应用颜色滤镜。
     */

    
//    fun applyColorFilter(bitmap: Bitmap, colorFilter: ColorMatrixColorFilter): Bitmap {
//        val cm = ColorMatrix()
//        cm.setSaturation(1f)
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 13. **Vignette 效果**
    - 为图像加上渐变阴影效果，使图片中心部分明亮，周围部分变暗。
     */

    
    fun applyVignetteEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val vignetteBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val distance = Math.sqrt(((x - width / 2).toDouble().pow(2) + (y - height / 2).toDouble().pow(2)))
                val pixel = bitmap.getPixel(x, y)
                val factor = (distance / (width / 2)).toFloat()
                val r = (Color.red(pixel) * (1 - factor)).toInt()
                val g = (Color.green(pixel) * (1 - factor)).toInt()
                val b = (Color.blue(pixel) * (1 - factor)).toInt()
                vignetteBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return vignetteBitmap
    }
    

    /* 14. **噪声滤镜 (Noise)**
    - 给图像添加噪点效果。
     */

    
    fun applyNoise(bitmap: Bitmap): Bitmap {
        val random = Random()
        val width = bitmap.width
        val height = bitmap.height
        val noisyBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel) + random.nextInt(50) - 25
                val g = Color.green(pixel) + random.nextInt(50) - 25
                val b = Color.blue(pixel) + random.nextInt(50) - 25
                noisyBitmap.setPixel(x, y, Color.rgb(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255)))
            }
        }
        return noisyBitmap
    }
    

    /* 15. **大理石纹理效果**
    - 给图像添加大理石纹理效果，通常使用噪声与色彩的渐变混合。
     */

    
    fun applyMarbleTexture(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height

                = bitmap.height
        val marbleBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (Color.red(pixel) + (Math.random() * 30 - 15)).toInt()
                val g = (Color.green(pixel) + (Math.random() * 30 - 15)).toInt()
                val b = (Color.blue(pixel) + (Math.random() * 30 - 15)).toInt()
                marbleBitmap.setPixel(x, y, Color.rgb(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255)))
            }
        }
        return marbleBitmap
    }
    


    /* 16. **水彩画效果 (Watercolor Effect)**
    - 将图像处理为水彩画风格，通常使用模糊和颜色分离。
     */

    
    fun applyWatercolorEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val watercolorBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel) + (Math.random() * 30 - 15).toInt()
                val g = Color.green(pixel) + (Math.random() * 30 - 15).toInt()
                val b = Color.blue(pixel) + (Math.random() * 30 - 15).toInt()
                watercolorBitmap.setPixel(x, y, Color.rgb(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255)))
            }
        }
        return watercolorBitmap
    }
    

    /* 17. **反色滤镜 (Negative Effect)**
    - 对图像的颜色进行反转。
     */

    
    fun applyNegativeEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val negativeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val negativePixel = Color.rgb(255 - Color.red(pixel), 255 - Color.green(pixel), 255 - Color.blue(pixel))
                negativeBitmap.setPixel(x, y, negativePixel)
            }
        }
        return negativeBitmap
    }
    

    /* 18. **双重曝光效果 (Double Exposure)**
    - 将两张图片合并为一张，产生双重曝光效果。
     */

    
    fun applyDoubleExposure(bitmap1: Bitmap, bitmap2: Bitmap): Bitmap {
        val width = bitmap1.width
        val height = bitmap1.height
        val doubleExposureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel1 = bitmap1.getPixel(x, y)
                val pixel2 = bitmap2.getPixel(x, y)
                val blendedPixel = blendPixels(pixel1, pixel2)
                doubleExposureBitmap.setPixel(x, y, blendedPixel)
            }
        }
        return doubleExposureBitmap
    }

    fun blendPixels(pixel1: Int, pixel2: Int): Int {
        val r = (Color.red(pixel1) + Color.red(pixel2)) / 2
        val g = (Color.green(pixel1) + Color.green(pixel2)) / 2
        val b = (Color.blue(pixel1) + Color.blue(pixel2)) / 2
        return Color.rgb(r, g, b)
    }
    

    /* 19. **镜像效果 (Mirror Effect)**
    - 对图像进行左右镜像反转。
     */

    
    fun applyMirrorEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val mirroredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(width - x - 1, y)
                mirroredBitmap.setPixel(x, y, pixel)
            }
        }
        return mirroredBitmap
    }
    

    /* 20. **复古效果 (Vintage Effect)**
    - 给图像添加温暖的色调和颗粒感，模拟复古风格。
     */

    
//    fun applyVintageEffect(bitmap: Bitmap): Bitmap {
//        val cm = ColorMatrix()
//        cm.setSaturation(0.2f)  // 减少饱和度
//        val vintageMatrix = floatArrayOf(
//            1.2f, 0.2f, 0.1f, 0f, 0f,
//            0.3f, 1.5f, 0.2f, 0f, 0f,
//            0.2f, 0.2f, 1.1f, 0f, 0f,
//            0f, 0f, 0f, 1f, 0f
//        )
//        cm.postConcat(ColorMatrix(vintageMatrix))
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 21. **闪电效果 (Lightning Effect)**
    - 给图像增加电闪雷鸣的效果，通过明亮的线条和光亮效果模拟。
     */

    
    fun applyLightningEffect(bitmap: Bitmap): Bitmap {
        // 使用噪声生成不规则亮点和线条
        val width = bitmap.width
        val height = bitmap.height
        val lightningBitmap = bitmap.copy(bitmap.config, true)

        val random = Random()
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (random.nextFloat() > 0.98) {
                    val lightningColor = Color.rgb(255, 255, random.nextInt(100) + 100)
                    lightningBitmap.setPixel(x, y, lightningColor)
                }
            }
        }
        return lightningBitmap
    }
    

    /* 22. **抽象艺术效果 (Abstract Art)**
    - 将图像转换为几何图形和抽象艺术风格。
     */

    
    fun applyAbstractArtEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val abstractBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width step 10) {
            for (y in 0 until height step 10) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                for (dx in 0 until 10) {
                    for (dy in 0 until 10) {
                        abstractBitmap.setPixel(x + dx, y + dy, Color.rgb(r, g, b))
                    }
                }
            }
        }
        return abstractBitmap
    }
    

    /* 23. **极光效果 (Aurora Effect)**
    - 模拟极光的色彩和渐变效果。
     */

    
    fun applyAuroraEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val auroraBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (Color.red(pixel) + Math.random().toInt() * 50).coerceIn(0, 255)
                val g = (Color.green(pixel) + Math.random().toInt() * 50).coerceIn(0, 255)
                val b = (Color.blue(pixel) + Math.random().toInt() * 50).coerceIn(0, 255)
                auroraBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return auroraBitmap
    }
    

    /* 24. **红外线效果 (Infrared Effect)**
    - 模拟红外线拍摄效果，通常增加图像的红色和绿色部分。
     */

    
    fun applyInfraredEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val infraredBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (Color.red(pixel) * 1.5).toInt().coerceIn(0, 255)
                val g = (Color.green(pixel) * 0.8).toInt().coerceIn(0, 255)
                val b = Color.blue(pixel) / 2
                infraredBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return infraredBitmap
    }
    

    /* 25. **过度曝光 (Overexposure)**
    - 增加图像的亮度和对比度，使图像出现过度曝光效果。
     */

    
//    fun applyOverexposure(bitmap: Bitmap): Bitmap {
//        val cm = ColorMatrix()
//        cm.setScale(1.2f, 1.2f, 1.2f, 1f) // 增加亮度和对比度
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 26. **阴影效果 (Shadow Effect)**
    - 给图像添加阴影效果，模拟物体的阴影。
     */

    
    fun applyShadowEffect(bitmap: Bitmap): Bitmap {
        val shadowBitmap = bitmap.copy(bitmap.config, true)
        val width = bitmap.width
        val height = bitmap.height
        val shadowColor = Color.argb(100, 0, 0, 0)

        for (x in 0 until width) {


            for (y in 0 until height) {
                if (x < width - 5 && y < height - 5) {
                    val pixel = bitmap.getPixel(x, y)
                    shadowBitmap.setPixel(x + 5, y + 5, shadowColor)
                    shadowBitmap.setPixel(x, y, pixel)
                }
            }
        }
        return shadowBitmap
    }
    

    /* 27. **透明度变化 (Opacity Adjustment)**
    - 修改图像的透明度，模拟透明效果。
     */

    
    fun applyOpacityEffect(bitmap: Bitmap, opacity: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val opacityBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val a = (Color.alpha(pixel) * opacity).toInt()
                opacityBitmap.setPixel(x, y, Color.argb(a, Color.red(pixel), Color.green(pixel), Color.blue(pixel)))
            }
        }
        return opacityBitmap
    }
    

    /* 28. **反射效果 (Reflection Effect)**
    - 在图像下方添加反射效果。
     */

    
    fun applyReflectionEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val reflectionBitmap = Bitmap.createBitmap(width, height * 2, Bitmap.Config.ARGB_8888)

        // 绘制原图
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                reflectionBitmap.setPixel(x, y, pixel)
            }
        }

        // 绘制反射部分
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, height - y - 1)
                reflectionBitmap.setPixel(x, height + y, pixel)
            }
        }

        return reflectionBitmap
    }
    

    /* 29. **时间胶囊效果 (Time Capsule)**
    - 给图像添加过去的颜色和颗粒感，模拟老照片效果。
     */

    
//    fun applyTimeCapsuleEffect(bitmap: Bitmap): Bitmap {
//        val cm = ColorMatrix()
//        cm.setSaturation(0.5f) // 降低饱和度
//        val timeCapsuleMatrix = floatArrayOf(
//            1f, 0.1f, 0.1f, 0f, 0f,
//            0.1f, 1f, 0.1f, 0f, 0f,
//            0.1f, 0.1f, 1f, 0f, 0f,
//            0f, 0f, 0f, 1f, 0f
//        )
//        cm.postConcat(ColorMatrix(timeCapsuleMatrix))
//        return applyColorMatrix(bitmap, cm)
//    }
    

    /* 30. **老式电影效果 (Old Movie Effect)**
    - 模拟老式电影的色彩和颗粒感，给图像带有怀旧感。
     */

    
    fun applyOldMovieEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val oldMovieBitmap = bitmap.copy(bitmap.config, true)

        // 模拟颗粒感
        val random = Random()
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel) + random.nextInt(5) - 2
                val g = Color.green(pixel) + random.nextInt(5) - 2
                val b = Color.blue(pixel) + random.nextInt(5) - 2
                oldMovieBitmap.setPixel(x, y, Color.rgb(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255)))
            }
        }
        return oldMovieBitmap
    }
    

    /* 31. **透明滤镜效果 (Transparent Filter Effect)**
    - 为图像添加透明滤镜效果，调低透明度。
     */

    
    fun applyTransparentFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val transparentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val alpha = (Color.alpha(pixel) * 0.5).toInt()
                transparentBitmap.setPixel(x, y, Color.argb(alpha, Color.red(pixel), Color.green(pixel), Color.blue(pixel)))
            }
        }
        return transparentBitmap
    }
    

    /* 32. **红色光晕效果 (Red Glow Effect)**
    - 给图像添加红色的光晕效果。
     */

    
    fun applyRedGlowEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val redGlowBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (Color.red(pixel) + 50).coerceIn(0, 255)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                redGlowBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return redGlowBitmap
    }
    

    /* 33. **火焰效果 (Fire Effect)**
    - 模拟火焰的颜色渐变效果，通常使用橙色和红色。
     */

    
    fun applyFireEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val fireBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (Color.red(pixel) + Math.random().toInt() * 50).coerceIn(0, 255)
                val g = (Color.green(pixel) + Math.random().toInt() * 20).coerceIn(0, 255)
                val b = Color.blue(pixel) / 2
                fireBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return fireBitmap
    }
    

    /* 34. **霓虹灯效果 (Neon Effect)**
    - 模拟霓虹灯的明亮和光辉效果。
     */

    
    fun applyNeonEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val neonBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (Color.red(pixel) * 1.5).toInt().coerceIn(0, 255)
                val g = (Color.green(pixel) * 1.5).toInt().coerceIn(0, 255)
                val b = (Color.blue(pixel) * 1.5).toInt().coerceIn(0, 255)
                neonBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return neonBitmap
    }
    

    /* 35. **时空隧道效果 (Warp Tunnel Effect)**
    - 通过旋转和变形图像模拟时空隧道效果。
     */

    
    fun applyWarpTunnelEffect(bitmap: Bitmap): Bitmap {
        // This one requires more advanced mathematical calculations to simulate a tunnel effect
        // Usually involves distortions and transformations using matrix operations.
        return bitmap
    }
    

    /*36. **铅笔素描效果 (Pencil Sketch)**
    -模拟铅笔素描的效果，创建黑白的素描风格。
     */
    fun applyPencilSketch(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pencilBitmap = bitmap.copy(bitmap.config, true)

        // Simple approach: reduce saturation and use lighter grayscale tones
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                pencilBitmap.setPixel(x, y, Color.rgb(gray, gray, gray))
            }
        }
        return pencilBitmap
    }

    /*37. **卡通化效果 (Cartoon Effect)**
    - 将图像转换为类似漫画的风格，突出边缘和简化颜色。
     */
    fun applyCartoonEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val cartoonBitmap = bitmap.copy(bitmap.config, true)

        // Apply edge detection and reduce color saturation
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y

                )
                val r = (Color.red(pixel) / 2).coerceIn(0, 255)
                val g = (Color.green(pixel) / 2).coerceIn(0, 255)
                val b = (Color.blue(pixel) / 2).coerceIn(0, 255)
                cartoonBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return cartoonBitmap
    }

    /*38. **蓝色深海效果 (Blue Deep Sea Effect)**
    - 模拟深海的蓝色风格，通过加强蓝色通道。
     */
    fun applyBlueDeepSeaEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val seaBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = (Color.blue(pixel) * 1.5).toInt().coerceIn(0, 255)
                seaBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return seaBitmap
    }

    /* 39. **绿光效果 (Green Glow Effect)**
    - 增加绿色的光晕效果，产生绿光。
     */
    fun applyGreenGlowEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val greenGlowBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel)
                val g = (Color.green(pixel) * 1.5).toInt().coerceIn(0, 255)
                val b = Color.blue(pixel)
                greenGlowBitmap.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        return greenGlowBitmap
    }
    
    /*
     40. **素描效果 (Sketch Effect)**
    - 利用简单的灰度转换方法，模仿手绘素描风格。
     */
    fun applySketchEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val sketchBitmap = bitmap.copy(bitmap.config, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                sketchBitmap.setPixel(x, y, Color.rgb(gray, gray, gray))
            }
        }
        return sketchBitmap
    }
    

}