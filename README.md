# MyEnergy - Project Vision

<img src="demo/1_login.png" alt="Use Demo Mode to check out the app without connecting a charger." width="135"> <img src="demo/2_status.png" alt="All data live at a glance. Say goodbye to long loading times!" width="135"> <img src="demo/5_smart_charging.png" alt="Smart charge planning with integrated calculator." width="135"> <img src="demo/6_settings.png" alt="Adapt the app to your needs!" width="135"> <img src="demo/7_dark_mode.png" alt="Dark mode for the nocturnals and OLED devices." width="135">

[📱 **show all demo images**](demo)

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
