# Визуализатор потребления ЖКУ
## Описание:
В базе данных храним 5 сущностей (получаем на вход от пользователя) :
1. Юзер (id, ФИО, и т.д.)
2. Показания ХВС (id, дата снятия показания, показание, id юзера)
3. Показания ГВС
4. Показания электроэнергии
5. Показания отопления

При запросе статистики за определенное время пользователь получает красивую диаграмму с потреблением услуг


## Пул задач:

1. Реализовать телеграм-бота, который принимает показатели и отправляет краткую сводку по полученным (id, date, value)
2. Реализовать 5 сущностей
    * User (id, Name, Address)
    * Cold water metrics (id, date, value, userId)
    * Hot water metrics (id, date, value, userId)
    * Electric power metrics (id, date, value, userId)
    * Heating metrics (id, date, value, userId)
3. Реализовать добавление полученных данных в бд
4. Реализовать фильтр данных: получение данных за заданный период времени (неделя, месяц и т.п.)
5. Реализовать построение диаграммы по полученным данным
6. Реализовать справочную команду в боте, которая ищет информацию об управляющей компании по адресу юзера
7. Реализовать изменение адреса (спросить у юзера, нужно ли оставить предыдущие записи с показаниями. Если нет - то удалить)

## Используемые технологии
- Spring Data JPA
- PostreSQL
- [Telegram Bot Java Library](https://github.com/rubenlagus/TelegramBots)