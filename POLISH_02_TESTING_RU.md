# Create: Tobacco — Polish 02 — чеклист тестирования

Тестировать после успешного применения `CreateTobacco_V1_Polish_02.patch`.
Не коммитить до зелёной сборки и критических проверок ниже.

## 0. Сборка и запуск

```powershell
.\gradlew.bat --stop
.\gradlew.bat build
```

Ожидается `BUILD SUCCESSFUL`.

После этого:

```powershell
.\gradlew.bat runClient
```

Проверить:
- Minecraft 1.21.1 загружается;
- NeoForge 21.1.228 загружается;
- Create 6.0.10 загружается;
- Create: Tobacco загружается;
- мир открывается без registry/datapack/enum-extension ошибки.

Если клиент падает на загрузке, сначала прислать `runs/client/logs/latest.log` и
файл из `runs/client/crash-reports`, если он появился.

## 1. Дикий табак — новая двухблочная форма

Worldgen проверять ТОЛЬКО в новых чанках. Лучше создать новый тестовый мир.

Полезные команды:

```mcfunction
/locate biome minecraft:plains
/locate biome minecraft:meadow
/locate biome minecraft:jungle
```

Ожидаемые зоны:
- Virginia: равнины/умеренные леса;
- Burley: луга/windswept/savanna;
- Havana: jungle/sparse jungle/bamboo jungle.

Проверить для каждого сорта:
- дикое растение имеет нижнюю и верхнюю половину;
- высота визуально два блока;
- верхняя и нижняя половины принадлежат одному растению;
- сломать нижнюю половину — растение полностью исчезает и дропается один раз;
- сломать верхнюю половину — растение полностью исчезает и дропается один раз;
- после генерации нет «висящей» верхней половины;
- после генерации нет одинокой нижней половины;
- растение стоит НА поверхности, а не на один блок ниже в случайной ямке;
- Havana не пытается массово появляться поверх листвы джунглей.

Частота Polish 02:
- Virginia: rarity 42;
- Burley: rarity 50;
- Havana: rarity 30 внутри более редких jungle-биомов.

Оценить субъективно:
- слишком часто;
- хорошо;
- слишком редко.

Нужное ощущение: долго идёшь без табака -> находишь заметную локальную заросль.

До отрисовки новых PNG missing texture на двух половинах ожидаема.

## 2. Дроп дикого табака

Сломать хотя бы 30–50 растений каждого сорта (можно тестировать командой
`/setblock`, если worldgen слишком медленный для статистики).

Ожидается с одного дикого растения:
- 1 соответствующее семя всегда;
- 25% шанс второго семени;
- 65% шанс одного Fresh Leaf;
- никогда не выпадает табак другого сорта;
- двухблочная структура не удваивает дроп.

Главная критическая проверка: одно растение не должно дать два комплекта дропа
только потому, что оно состоит из двух блоков.

## 3. Экономика выращиваемого табака

Для быстрого теста:

```mcfunction
/give @s create_tobacco:virginia_seeds 32
/give @s create_tobacco:burley_seeds 32
/give @s create_tobacco:havana_seeds 32
/give @s minecraft:bone_meal 64
```

Для каждого сорта:
- посадить на farmland;
- вырастить до age=7;
- зрелый куст всегда даёт 2 Fresh Leaves;
- примерно 35% зрелых кустов дают третий Fresh Leaf;
- одно семя всегда возвращается;
- примерно 35% зрелых кустов дают второе семя;
- незрелое растение не даёт Fresh Leaves, но возвращает одно семя.

Не менять Millstone-рецепт в этом тесте: его выход остался прежним.

## 4. Дёргание зажжённого предмета

```mcfunction
/give @s create_tobacco:craftmel
/give @s minecraft:flint_and_steel
```

Поджечь Craftmel и просто держать в руке минимум 20–30 секунд.

Ожидается:
- burn timer продолжает уменьшаться;
- tooltip обновляется;
- предмет НЕ делает маленький re-equip рывок каждую секунду;
- настоящая смена hotbar-слота по-прежнему имеет обычную анимацию Minecraft.

Отдельно начать затяжку около момента обновления burn timer:
- использование не должно сбрасываться из-за изменения Data Component;
- одна успешная затяжка снимает ровно одну puff;
- passive burn не должен одновременно снять вторую puff во время активной затяжки.

## 5. Анимация — первое лицо

Использовать любую зажжённую сигарету.

Ожидается:
- первые ~6 ticks рука плавно идёт к лицу;
- затем положение стабильно до завершения 1.2 сек;
- предмет заметно ближе к горизонтальной ориентации;
- нет vanilla DRINK bob;
- после завершения рука возвращается нормально;
- ранний отпуск не расходует puff.

Записать замечания максимально конкретно:
- слишком высоко/низко;
- слишком близко/далеко;
- повернуть ещё по/против часовой;
- горизонтальность хорошая/недостаточная.

## 6. Анимация — третье лицо

Включить F5. Желательно также посмотреть вторым клиентом.

Ожидается:
- больше не используется визуально узнаваемая поза `TOOT_HORN`;
- одна рука поднята к области рта;
- поза не дёргается;
- другой игрок видит её во время фактического использования;
- когда игрок не курит, обычная поза руки возвращается.

Особенно проверить правую и левую руку, если используешь сигарету в off-hand.

## 7. Постепенное облегчение Withdrawal

```mcfunction
/createtobacco reset @s
/createtobacco withdrawal trigger @s severe
/effect list @s
```

Должен появиться Withdrawal IV.

Взять зажжённую сигарету и сделать четыре успешные затяжки по одной, проверяя
`/effect list @s` после каждой:

1. Severe / Withdrawal IV
2. после первой puff -> Withdrawal III
3. после второй puff -> Withdrawal II
4. после третьей puff -> Withdrawal I
5. после четвёртой puff -> Withdrawal исчезает

Важно:
- duration не должен каждый раз начинаться заново с 60 секунд;
- Nausea не должна заново роллиться при снижении уровня;
- частично снятая Withdrawal не сбрасывает craving timer;
- только ПОЛНОЕ выкуривание предмета сбрасывает `activeTicksSinceSatisfied`.

Проверить:

```mcfunction
/createtobacco status @s
```

## 8. Creperfield Microblast Polish 02

```mcfunction
/createtobacco effect trigger @s creperfield
```

Проверить рядом с мобами на расстояниях примерно 2, 4 и 5 блоков.

Ожидается:
- радиус воздействия около 5 блоков;
- knockback немного сильнее Polish 01;
- частиц заметно меньше;
- визуальный взрыв не выглядит намного больше фактического радиуса;
- блоки не ломаются;
- огонь не создаётся;
- smoker не получает explosion damage;
- Create belts/storage/contraptions не повреждаются;
- Speed II + Haste II остаются прежними.

## 9. Natural smouldering regression

Поджечь сигарету и оставить в обычном inventory:
- одна natural puff примерно за 60 секунд;
- сигара примерно за 90 секунд;
- natural burn не даёт brand proc;
- natural burn не даёт dependence;
- natural burn не уменьшает Withdrawal;
- natural burn не выдаёт completion reward.

Положить зажжённый предмет в Портсигар:
- закрыть Портсигар;
- подождать;
- burn timer внутри не должен идти (дизайн Polish 01 сохранён).

## 10. Пачки и Портсигар — короткий regression

Полную производственную проверку повторять необязательно, если Polish 01 уже был
пройден. Достаточно:
- скрафтить одну бренд-пачку Mechanical Crafting 4x3;
- извлечь одну сигарету;
- открыть Портсигар;
- положить fresh, partial и lit smoking item;
- закрыть/открыть;
- компоненты сохранены;
- 15 слотов на месте.

## 11. RNG regression

Никакие изменения Polish 02 не должны переносить random roll в `onUseTick`.

Проверить:
- раннее отпускание не вызывает proc;
- один successful puff даёт максимум один roll;
- Creperfield/KEnd/Bedromorkanal не прокают многократно от одного удержания.

Для функциональной проверки по-прежнему использовать debug-команды.

## 12. Dedicated server smoke test

```powershell
.\gradlew.bat runServer
```

Проверить с клиентом:
- мир загружается с enum extension в mod metadata;
- dedicated server не пытается загрузить client-only HumanoidModel классы;
- wild worldgen работает в новых чанках;
- Withdrawal downgrade работает;
- Creperfield работает;
- Портсигар открывается и сохраняется;
- server log не содержит client-class linkage error.

## 13. Если снова случится краш

Сразу после краша НЕ перезаписывать/удалять `runs/client`.

Проверить:

```powershell
Get-ChildItem .\runs\client\crash-reports -File -ErrorAction SilentlyContinue |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 10 Name,LastWriteTime,Length

Get-ChildItem .\runs\client\logs -File -ErrorAction SilentlyContinue |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 15 Name,LastWriteTime,Length

Get-ChildItem . -Recurse -File -Filter "hs_err_pid*.log" -ErrorAction SilentlyContinue |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 10 FullName,LastWriteTime,Length
```

Прислать:
1. самый свежий `crash-*.txt`, если он есть;
2. `runs/client/logs/latest.log`;
3. если `latest.log` уже от следующего запуска — приложить предыдущий `.log.gz`;
4. `hs_err_pid*.log`, если такой появился.

## 14. Коммит только после проверки

Если сборка, клиент и критические пункты зелёные:

```powershell
git status
git add .
git commit -m "V1 polish: refine wild tobacco and smoking feedback"
```
