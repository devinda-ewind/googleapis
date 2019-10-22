[![](https://jitpack.io/v/devinda-ewind/googleapis.svg)](https://jitpack.io/#devinda-ewind/googleapis)

# Google APIs

Features
------
  - Google Place Picker Activity
  - Google Map
  - Google Location
  - Google Place Picker (AutoComplete text View)
-------
Download
-------
Use Gradle:
**Step 1.**  Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
**Step 2.** Add the dependency
```gradle
dependencies {
	        implementation 'com.github.devinda-ewind:googleapis:version'
	}
```

Or Maven:

```xml
<repositories>
	<repository>
		   <id>jitpack.io</id>
		   <url>https://jitpack.io</url>
	</repository>
</repositories>
<dependency>
	    <groupId>com.github.devinda-ewind</groupId>
	    <artifactId>googleapis</artifactId>
	    <version>Tag</version>
</dependency>
```

Google Place Activity(Extension)
-------
**initialize places api in application**
```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, getString(R.string.google_map_key))
    }
}
```
**Start place picker activity**
```kotlin
 override fun onClick(v: View?) {
        when (v?.id) {
            R.id.et_address -> {
                startPlacePicker()
            }
        }
} 
```

Get result from  **getLocationFromResult(requestCode, resultCode, data)**
```kotlin
override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        context?.getLocationFromResult(requestCode, resultCode, data)
            ?.let {
                et_address.setText(it.address)
                latitude = it.latitude
                longtude = it.longtude
            }
    }
```
------
Google Place Picker (AutoComplete text View)
---------
Initialize **googlePlackPickerManager**
```kotlin
    val googlePlacePickerManager = GooglePlacePickerManager(context)
    
    internal fun initPlaceSearch(completeTextView: AutoCompleteTextView) {
        googlePlacePickerManager.initialPlace(
            completeTextView,
            object : GooglePlacePickerCallback() {
                override fun googlePlacePickerCallback(place: Place) {
                    //selected google place
                }
            })
    }
```
**Get Address from LatLng**

```kotlin
    val googlePlacePickerManager = GooglePlacePickerManager(context)
    
    fun...{
        googlePlacePickerManager.initialGeoCode(latlng)
    }

    internal fun initPlaceSearch(completeTextView: AutoCompleteTextView) {
        googlePlacePickerManager.initialPlace(
            completeTextView,
            object : GooglePlacePickerCallback() {
                override fun googleAddressCallback(googleLocation: GoogleLocation) {
                    //
                }
            })
    }
```

