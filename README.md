

# Praca dyplomowa inżynierska "*Zastosowanie analizy obrazu twarzy do sterowania aplikacją na urządzeniach z systemem Android*"

**Autor:** Adamski Maciej<br/>
**Promotor:** prof. dr hab. inż. Przemysław Rokita<br/>
**Uczelnia/wydział/instytut**: Instytut Informatyki na Wydziale Elektroniki i Nauk Informacyjnych, Politechniki Warszawskiej<br />
**Tytuł**: Zastosowanie analizy obrazu twarzy do sterowania aplikacją na urządzeniach z systemem Android<br />
**Tytuł angielski**: Facial image based application control on Android devices

 
 ## Streszczenie

<p align="justify">Celem pracy inżynierskiej było stworzenie aplikacji na urządzenia z systemem Android, która przy pomocy analizy twarzy będzie reagowała na gesty użytkownika takie jak mruganie czy ruch gałkami ocznymi.
 
 Został przygotowany zestaw algorytmów do detekcji poszczególnych fragmentów twarzy, które następnie były testowane pod kątem skuteczności detekcji i złożoności czasowej. Do implementacji wybranych metod przetwarzania obrazu zostały użyte biblioteki OpenCV oraz Dlib. Najlepsze rozwiązania z poszczególnych grup zostały wykorzystane w finalnym projekcie oprogramowania. Proces detekcji twarzy odbywa się przy pomocy algorytmu opartego na Histogramach zorientowanych gradientów} natomiast znaczniki są wykrywane dzięki metodzie Kazemi. Punkty charakterystyczne twarzy pozwalają określić położenie na zdjęciu oczu, a także stwierdzić czy są one zamknięte przy użyciu współczynnika Eye Aspect Ratio. Wykorzystywane jest progowanie na podstawie dystrybuanty celem wyznaczenia środka źrenicy.
 
Końcowym efektem pracy dyplomowej jest prosta aplikacja, która prezentuje działanie analizy obrazu oraz reaguje na ruch oczami w poziomie czy mruganie użytkownika. Wykrycie takich gestów przedstawione było w zrozumiałej formie przez wyświetlanie komunikatów oraz przesuwanie obrazów w prostej galerii zdjęć. 

 Praca pomogła zapoznać się z wytwarzaniem oprogramowania użytkowego na systemy Android. Przyniosła również dużo wiedzy z zakresu przetwarzania obrazu oraz sieci neuronowych i uczenia maszynowego. Pozwoliła też poznać dwie popularne biblioteki z dziedziny widzenia komputerowego. </p>

**Słowa kluczowe**: Cyfrowe przetwarzanie i analiza obrazów, Detekcja twarzy, Śledzenie oczu, Programowanie aplikacji mobilnych, Kontrola aplikacji z wykorzystaniem obrazów

## Streszczenie angielskie

<p align="justify">The main goal of this thesis was to create the application for the devices that run the Android operating system, which with the aid of face analysis, would be able to react to the user gestures such as blinking or eyeball movement.

To detect specific parts of the face there was prepared a set of algorithms. Each of them was further tested in terms of detection effectiveness and time complexity. The chosen image processing methods were implemented with the usage of OpenCV and Dlib libraries. The best solutions from each group were used in a final software project. The face detection process is performed with the aid of an algorithm based on a Histogram of Oriented Gradients, whereas the facemarks are being detected with the Kazem} method. The face pointers allow determining the eyes' location on the photo as well as ascertain whether they are closed with the Eye Aspect Ratio coefficient. There is used the thresholding by cumulative distribution function in order to find the center of the pupil.

The final result of this thesis is a simple application that presents the working of the image analysis, reacts to horizontal eyeballs movement, and to blinking. Those gestures detection were shown to the user in a readable way as a notification and also in the form of moving images in the simple photo gallery.

This work was a helpful introduction to application development on Android devices. It also brought a significant amount of knowledge related to image processing, neural networks, and machine learning. Moreover, it allowed getting to know the two popular libraries from the domain of computer vision. </p>

**Keywords**: Digital image processing and analysis, Face detection, Eye gaze tracking, Mobile application development, Image based application

 ## Katalogi

- */app/*  - kod aplikacji  
- */doc/*  - praca dyplomowa w formacie LaTeX oraz skompilowana do postaci doku-  
mentu pdf. W szczególności:
-- Wygenerowana praca dyplomowa w formacie PDF - [link](doc/Praca_dyplomowa.pdf). 
-- Kod źródłowy pracy dyplomowej w formacie LaTeX (TeX) - [link](doc/main.tex)
- */demo_examples/*  - nagrania i zrzuty z aplikacji prezentujące najważniejsze funkcjonalności projektu  


## Instrukcja kompilacji

 1. W programie *Android Studio* utworzyć nowy projekt wykorzystując opcję *Get from VCS*.
 2. Wybrać następujące opcje
 -- Version control: `Git`
 -- URL:  `https://github.com/madamskip1/Engi_thesis_Android_Camera_control.git`.
 3. W oknie *Build Variants* wybrać tryb budowania `release` dla modułów: *:app* oraz *:opencv*.
 4. W konfiguracji *Run/Debug Configurations* w zakładce *General/Installation Options* ustawić flagę instalacji (*Install Flags*) na `-g`.
 5. Projekt jest gotowy do utworzenia, a następnie instalacji na urządzeniu mobilnym.