# MyEnergy - Project Vision

MyEnergy is an App for monitoring power usage in a Smart Home and controlling electric vehicle (ev) charging.
It serves as an alternative to [SMAs](https://www.sma.de/) proprietary app [SMA Energy](https://play.google.com/store/apps/details?id=de.sma.energy).
Contrary to the original app, it should directly communicate with the ev charger over local area network instead of relying on cloud services.
That way MyEnergy should overcome or minimize the following usability deficiencies of SMA Energy: 

* long response times
* display of outdated data
* refreshing of data at low rates

Additionally the app should provide the following features to improve the user experience:

* intuitive calculator for ev charge  
* alert if charging is interrupted _(optional)_  

While still retaining the feature set of SMA Energy, including:

* Status page, displaying current energy consumption
* monitoring and controlling of ev charging
* displaying historic data in graphs _(out of scope)_

The ev charger has an undocumented RestAPI which can be reverse engineered for communication over LAN.  
Inverter and Energy Management could be accessed over [ModbusTCP](https://en.wikipedia.org/wiki/Modbus), using [digitalpetri/modbus](https://github.com/digitalpetri/modbus) library or similar, but the ev charger should expose all needed data.

## UI Preview

<img src="ui_preview/status.jpg" title="" alt="" width="271">

_status activity_

<img src="ui_preview/charging.png" title="" alt="" width="271">

_charging controller& calculator activity_