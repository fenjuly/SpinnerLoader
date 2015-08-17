# SpinnerLoader
A loader that measures up Material Design.
This is an implemention of [http://t.co/sUuYTeKOzp](http://t.co/sUuYTeKOzp)

![SpinnerLoader](http://i.imgur.com/qFNRkHM.gif)

###Usage
only one attribute `point_color`

```xml
<com.fenjuly.mylibrary.SpinnerLoader
        android:id="@+id/one"
        android:layout_marginTop="30dp"
        android:layout_centerHorizontal="true"
        app:point_color="@color/accent_material_dark"
        android:layout_width="40dp"
        android:layout_height="40dp"/>
```

and one method `public void setPointcolor(int color)`

```java
setPointcolor(color);
```

###License
License under [MIT](https://github.com/fenjuly/SpinnerLoader/raw/master/LICENSE)
