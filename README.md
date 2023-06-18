# Listen With a Friends
Документация по разработке
# Стиль кода
1. Переменные и методы должны быть в стиле lowerCamelCase, классы в UpperCamelCase, нежелательно использование нижнего подчеркивания в названиях.
2. Названия xml файлов должны быть выполнены в стиле snake_case и по шаблону `rootname_filename.xml`, например `activity_base.xml`, `dialog_loto.xml`, `ic_balance.xml` и т.д.
   - `activity_xxx.xml` - шаблон названия для всех activity;
   - `dialog_xxx.xml` - шаблон названия для всех диалогов;
   - `fragment_xxx.xml` - шаблон названия для всех frament;
   - `item_xxx.xml` - шаблон названия для всех элементов ListView или RecyclerView;
   - `ic_xxx.xml` - шаблон названия для всех drawable иконок.
# Элементы программирования
1. Воздержитесь от добавления в dependencies каких либо зависимостей и библиотек. Все можно сделать стандартными средствами Java и Android Studio. При необходимости, реализуйте нужные методы самостоятельно (см. п. 2, 3).
2. Многие полезные методы были написаны вручную, смотрите в пакете utils:
   - `ArrayUtils` - предоставляет функции для работы с листами;
   - `FileUtils` - предоставляет функции для работы с файлами. Пока что не используется в масштабных целях;
   - `ImageUtils` - предоставляет функции для работы с изображениями;
   - `JsonUtils` - предоставляет функции для работы с JSON;
   - `StringUtils` - предоставляет функции для работы со строками;
   - `TimeUtils` - предоставляет функции для работы со временем.
3. Можно добавить свои utils, если это поможет избежать ненужных импортов. 
4. При создании и редактировании activity НЕ используйте ConstraitLayout. Никогда. Вместо него используйте LinearLayout.
5. При создании и редактировании activity избегайте хардкода в элементах с текстом. Добавляйте нужные вам строки в `string.xml`.
# Диалоги
Диалоги - вызываемые pop-up окна, не требующие смены activity.
Типы диалогов, которые могут понадобиться при разработке:
- `DialogTextBox` - базовый диалог, который позволяет вывести некий текст;
- `menuDialog/MenuDialogFragment` - диалог, с помощью которого можно вызвать список кнопок. Пример:
![MenuDialogFragment](https://github.com/LWAFapp/android/assets/74586660/abea777a-0a88-43b3-ad73-7809e11a64d7)
Вызов диалогов осуществляется следующим образом
```java
import com.Zakovskiy.lwaf.DialogTextBox
...
new DialogTextBox(context, title, content).show();
ИЛИ
new DialogTextBox(context, content).show();
```
```java
import com.Zakovskiy.lwaf.menuDialog.MenuButton;
import com.Zakovskiy.lwaf.menuDialog.MenuDialogFragment;
import java.util.List;
import java.util.ArrayList;
...
List<MenuButton> btns = new ArrayList<>();
btns.add(new MenuButton(content, color, functionOnClick));
MenuDialogFragment.newInstance(context, btns).show(getFragmentManager(), "MenuButtons");
```
# Сокеты
Сетевая часть LWaF построена на сокетах. При создании класса, требующего обращения к сервару LWaF, необходимо следовать данной инструкции:
```java
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;

private SocketHelper socketHelper = SocketHelper.getSocketHelper();

@Override
public void onStart() {
  ...
  socketHelper.subscribe(this);
  HashMap<String, Object> data = new HashMap<>();
  data.put(PacketDataKeys.EVENT_TYPE, EVENT_CONTENT);
  this.socketHelper.sendData(new JSONObject(data));
}

@Override
public void onStop() {
  this.socketHelper.unsubscribe();
}

@Override
public void onReceive(JsonNode json) {
  if (json.has(PacketDataKeys.TYPE_EVENT)) {
    String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
    switch (typeEvent) {
      case "some_event_code1":
        ...
        break;
      case "some_event_code2":
        ...
        break;
    }
  }
}
```
Все типы событий можно найти в `utils/PacketDataKeys`.
