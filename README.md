# Мобильное приложения “Доска объявлений” для Android
Мобильное приложение “Доска объявлений” построена по клиент-серверной архитектуре и состоит из двух компонентов: 
1) Android-приложение; 
2) Сервер и база данных Firebase.

Для подключения данного проекта на свой компьюетр необходимо:
1. Зарегистрировать на Firebase - https://firebase.google.com/
![image](https://user-images.githubusercontent.com/107750235/174432897-962be8a4-9640-48ab-8868-5c1acfd33808.png)
2. Создать новый проект.

![image](https://user-images.githubusercontent.com/107750235/174432998-a986bfe1-7041-46cd-8d68-33c6d0fff360.png)

3. Подключить к проекту возможность Аутенфикации через Email/Goggle account.

![image](https://user-images.githubusercontent.com/107750235/174433140-c28c9ab8-e364-4a72-acea-0b3efa8d5cb2.png)

4. Подключить к проекту Realtime Database и Storage.


5. Сгенерировать в Android Studio ключ SHA-1 и добавить его в Firebase.( Генерация происходит через Gradle с помощью комманды - signingReport)

6. Подключить Firebase к проекту Android Studio с помощью Tools.

![image](https://user-images.githubusercontent.com/107750235/174433226-afe2c735-5750-43a5-bff6-8da227b989cb.png)

7. Скачать с Firebase файл goole-services.json и добавить его в проект Android.

![image](https://user-images.githubusercontent.com/107750235/174433264-5abd8c33-af7b-486e-8822-7f039a919793.png)




