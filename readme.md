<p align="center"><img src="/preview/header.png" height=500></p>

SegmentedProgressBar
=================

[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
<br>

This is an Android project allowing to create a segmented progress bar similar to tik-tok.


USAGE
-----

To make a Segmented ProgressBar in your layout XML and to use it add Segmented ProgressBa library in your project or you can also grab it via Gradle:

```groovy
implementation 'com.github.Captaincoder1111:Segmented-Progress-Bar:1.0'
```

XML
-----

```xml
<com.example.segmentedprogressbar.SegmentedProgressBar
        android:id="@+id/segmentedbar"
        android:layout_width="match_parent"
        android:layout_height="45dp">

    </com.example.segmentedprogressbar.SegmentedProgressBar>
```

KOTLIN
-----

```kotlin
        //Set Timer
        segmentedbar.enableAutoProgressView(8000)
        //Set Diver Color
        segmentedbar.setDividerColor(Color.WHITE)
        //Divider Enabled
        segmentedbar.setDividerEnabled(true)
        //Set Divider Width
        segmentedbar.setDividerWidth(4f)
        
        //Set Divider fill color
        segmentedbar.setShader(
            intArrayOf(
                Color.CYAN,
                Color.CYAN,
                Color.CYAN
            )
        )
        
          //Resume/Start progress bar 
           segmentedbar.resume()
          //Pause the progress 
           segmentedbar.pause()
           //Add divider 
           segmentedbar.addDivider()              
        ```

### Listener (in Kotlin)
segmentedbar.SetListener(object :
            ProgressBarListener {
            override fun TimeinMill(mills: Long) {
                var sec_passed = (mills / 1000).toInt()
                if (sec_passed > 7) {
                   //do your thing
                }
            }
        })
```Usage Example
btngo.setOnTouchListener(object:View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                when(p1!!.action)
                {
                    ACTION_DOWN ->{
                        //Resume/Start progress bar 
                        segmentedbar.resume()
                    }
                    ACTION_UP ->{
                        //Pause the progress 
                        segmentedbar.pause()
                        //Add divider 
                        segmentedbar.addDivider()
                    }
                }
                return true
            }
        })
```
