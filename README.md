# 🦄 Wonderful FreshAir Application
[![Java CI with Gradle](https://github.com/AdevintaSpain/wonderful-freshair-app/actions/workflows/gradle.yml/badge.svg)](https://github.com/AdevintaSpain/wonderful-freshair-app/actions/workflows/gradle.yml)

Sample to play with [Kotlin](https://kotlinlang.org/) & [Arrow](https://arrow-kt.io/)

## Run

Get your OpenWeatherMap API key from [https://openweathermap.org/api]()

* Linux/MacOS
```bash
export OWM_APIKEY=xxxx
./freshair --city Barcelona,ES Paris,FR London,UK
```

* Windows
```powershell
$Env:OWM_APIKEY="xxxx"
.\freshair.bat --city Barcelona,ES Paris,FR London,UK
```

## Use Cases

![air-quality-usecase](doc/air-quality-usecase.png)
