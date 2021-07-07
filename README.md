# 🦄 Wonderful FreshAir Application
[![Java CI with Gradle](https://github.com/AdevintaSpain/wonderful-freshair-app/actions/workflows/gradle.yml/badge.svg)](https://github.com/AdevintaSpain/wonderful-freshair-app/actions/workflows/gradle.yml)

Sample to play with [Kotlin](https://kotlinlang.org/) & [Arrow](https://arrow-kt.io/)

## How it was made?

* Session #1 "Modelando Ausencia de Valor" [on youtube](https://youtu.be/q52oo2KOQYo) and [pull request changes](https://github.com/AdevintaSpain/wonderful-freshair-app/pull/1)

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
