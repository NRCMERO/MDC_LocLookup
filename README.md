This application works with your mobile data capture application like Kobo and [Mobenzi](https://www.mobenzi.com/). It will look up the name of the area where you are and return the name of that location to the mobile data capture application.

### How it works

It fetches user's accurate location from GPS, then searches the selected map shape files for the area name that the user is located in, returns it to the mobile data capture application. The GPS location will not be cached anywhere. Also, the app can completely run offline.

The name consists of one or more parts called 'fields', separated by a semi-colon (;). The user can specify what fields will form the name. And this can be done once the map is added, also from the popup menu.

### Usage instructions

#### First Run

When running the app for the first time, a welcome message will appear, and maps list will be empty.
You can start adding maps by clicking on the + icon at the header of the application:

![Main Screen](https://raw.githubusercontent.com/NRCMERO/MDC_LocLookup/master/LocLookup/screenshots/sc1.JPG)

#### Showing Pop-Up Menu

You can show a list of operations that can be applied on the map after successfully being added, via long-clicking on the map item. For every map you can: edit name, edit fields, and permanently delete.

#### Selecting The Active Map

A single click on a map item will select that map, a ✓ will appear on the selected map. Above, for example, Pakistan is selected. Selecting a map means that the app will look for your location only in that map.

#### Picking a Map File

When you click on the add icon mentioned before, a file browser will opened and you can go and select the .zip file that contains map shape files.
A valid .zip file has to contain one, and only one file of the following types: *.shp, *.shx, and *.dbf. Missing one of these files or finding any duplicate will lead to a non-acceptable selection. Having redundant files is ok, they will be simply ignored.

More about shape files: [Wikipedia](https://en.wikipedia.org/wiki/Shapefile)  
Where they can be found: [Site #1](https://data.humdata.org/dataset)  

If you have Android M operating system or later on your phone, you will see permission request dialogs, please allow required permissions in order to have the app working properly.

When shape files are validated, a dialog with a list of all available fields is shown. The fields are a set of information related to the areas provided within the shape files. You can select the fields that you like to be returned to the mobile data capture application.

![Fields Selection](https://raw.githubusercontent.com/NRCMERO/MDC_LocLookup/master/LocLookup/screenshots/sc2.JPG)

### Running The Project

This project has been created using Android Studio IDE. For running this project, you'll need to install Android Studio and android SDK on your machine. It's completely free. You can directly go to their [official website](https://developer.android.com/studio/index.html) and find more details.

A compiled apk can be found in 'apk' directory directly in the project main directory, so you can install it on your phone if you don't like to open the project in Android Studio.

### Supported Android Version

You Android 4.2 or higher installed on your phone to be able to run the application.

### Dependencies

Here's a list of the non-native dependencies that have been used in the project:

* [Map-Utils](https://github.com/googlemaps/android-maps-utils)
* [Droppy](https://github.com/shehabic/Droppy)
* [Materialish-Progress](https://github.com/pnikosis/materialish-progress)
* [Material-File-Picker](https://github.com/nbsp-team/MaterialFilePicker)

### Resources

* [Icon](https://www.iconfinder.com/icons/299035/marker_icon)

* [Font](https://github.com/Gue3bara/Cairo)
