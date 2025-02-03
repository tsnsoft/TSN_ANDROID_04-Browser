package kz.talipovsn.browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    WebView webView; // Объявление переменной для работы с WebView
    ProgressBar progressBar; // Объявление переменной для работы с ProgressBar
    EditText urlText; // Объявление переменной для указания адреса страницы
    EditText searchText; // Объявление переменной для указания текста поиска
    TextView searchCountText; // Объявление переменной для отображения количества найденных текстов

    // Объявление переменных для работы с кнопками навигации
    ImageButton backButton, forwardButton, refreshButton, loadButton, forwardSearchButton;

    FloatingActionButton searchButton; // Объявление переменной для работы с кнопкой поиска в webView

    ImageButton searchCloseButton, searchBackButton, homeButton; // Объявление переменных для работы с кнопками поиска в webView

    RelativeLayout mainToolLayout, searchToolLayout; // Объявление переменных для работы с панелями инструментов
    MyViewClient myViewClient = new MyViewClient(); // Объявление переменной для работы с собственным загрузчиком html

    final int MAX_100 = 100; // Объявление переменной для работы с ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Установка темы
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        // Привязка переменных к элементам в разметке
        urlText = findViewById(R.id.urlText);
        loadButton = findViewById(R.id.loadButton);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        forwardButton = findViewById(R.id.forwardButton);
        homeButton = findViewById(R.id.homeButton);
        searchButton = findViewById(R.id.searchButton);
        mainToolLayout = findViewById(R.id.mainToolLayout);
        searchToolLayout = findViewById(R.id.searchToolLayout);
        forwardSearchButton = findViewById(R.id.forwardSearchButton);
        searchBackButton = findViewById(R.id.searchBackButton);
        searchCloseButton = findViewById(R.id.searchCloseButton);
        searchText = findViewById(R.id.searchText);
        searchCountText = findViewById(R.id.searchCountText);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);

        searchToolLayout.setVisibility(View.GONE); // Скрываем панель поиска
        progressBar.setMax(MAX_100); // Устанавливаем максимальное значение ProgressBar
        progressBar.setVisibility(View.GONE); // Скрываем ProgressBar

        urlText.setFocusable(false); // Запрещаем фокусировку на адресе страницы
        urlText.setFocusableInTouchMode(false); // Запрещаем фокусировку на адресе страницы в режиме прикосновения
        urlText.setTextIsSelectable(true); // Разрешаем выделение адреса страницы
        urlText.setHighlightColor(Color.MAGENTA); // Устанавливаем цвет выделения адреса страницы

        webView.requestFocus(); // Устанавливаем фокус на webView

        webView.getSettings().setJavaScriptEnabled(true); // Включаем поддержку JavaScript
        webView.getSettings().setSupportZoom(true); // Включаем поддержку масштабирования
        webView.getSettings().setBuiltInZoomControls(true); // Включаем встроенные кнопки масштабирования
        webView.getSettings().setLoadWithOverviewMode(true); // Включаем режим просмотра страницы
        webView.getSettings().setUseWideViewPort(true); // Включаем широкий режим просмотра страницы
        //webView.getSettings().setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // Устанавливаем стиль полос прокрутки внутри оверлея
        webView.setBackgroundColor(Color.WHITE); // Устанавливаем цвет фона webView

        webView.setWebViewClient(myViewClient); // Устанавливаем собственный загрузчик html

        // Обработчик отображения загрузки страниц
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) { // Переопределение метода отображения прогресса загрузки страницы
                progressBar.setProgress(progress); // Устанавливаем значение ProgressBar
                // Если страница загружается и ProgressBar скрыт, то показываем ProgressBar
                if (progress < MAX_100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                // Если страница загружена и ProgressBar показан, то скрываем ProgressBar
                if (progress == MAX_100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        // Добавление меню с возможностью загрузки картинок
        registerForContextMenu(webView);

        // Восстановление состояния просмотра (страницы) в webView
        if (savedInstanceState != null) { // Если состояние активности сохранено
            webView.restoreState(savedInstanceState); // Восстанавливаем состояние webView
        } else {
            webView.loadUrl(getString(R.string.home_page)); // Иначе загружаем домашнюю страницу
        }

        // Обработчик кнопки загрузки страницы
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Скрываем клавиатуру телефона
                hideSoftInput();
                // Загружаем страниу в webView
                webView.loadUrl(urlText.getText().toString());
            }
        });

        // Обработчик кнопки Назад
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) { // Если можно вернуться назад
                    webView.goBack(); // Возвращаемся назад
                }
            }
        });

        // Обработчик кнопки Вперед
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoForward()) { // Если можно перейти вперед
                    webView.goForward(); // Переходим вперед
                }
            }
        });

        // Обработчик кнопки Обновить
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.reload();
            } // Обновляем страницу
        });

        // Обработчик кнопки Домой
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Скрываем клавиатуру
                hideSoftInput();
                // Загружаем страниу в webView
                webView.loadUrl(getString(R.string.home_page));
                // Показываем адрес домашней страницы
                urlText.setText(getString(R.string.home_page));
            }
        });

        // Обработчик кнопки Поиск
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCountText.setText(""); // Очищаем текст с количеством найденных текстов
                mainToolLayout.setVisibility(View.GONE); // Скрываем панель инструментов
                searchToolLayout.setVisibility(View.VISIBLE); // Показываем панель поиска
            }
        });

        // Обработчик кнопки Поиск Вперед
        forwardSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.findNext(true);
            } // Ищем следующий текст
        });

        // Обработчик кнопки Поиск Назад
        searchBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.findNext(false);
            } // Ищем предыдущий текст
        });

        // Обработчик нажатий кнопок в окошке поиска
        searchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Если нажата клавиша Enter
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER))) {
                    // Скрываем клавиатуру
                    hideSoftInput();
                    // Ищем нужный текст в webView
                    webView.findAll(searchText.getText().toString());
                    // Активируем возможность отображения найденного теккста в webView
                    try {
                        Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE); // Получаем метод setFindIsUp
                        m.invoke(webView, true); // Вызываем метод setFindIsUp
                    } catch (Exception ignored) {
                    }
                }
                return false;
            }
        });

        // Обработчик поиска в WebView
        webView.setFindListener(new WebView.FindListener() {
            // Переопределение метода получения результата поиска
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                searchCountText.setText(""); // Очищаем текст с количеством найденных текстов
                if (numberOfMatches > 0) { // Если найден хотя бы один текст
                    // Отображаем количество найденных текстов
                    searchCountText.setText(String.format("%d %s %d", activeMatchOrdinal + 1, getString(R.string.iz), numberOfMatches));
                } else {
                    // Отображаем, что ничего не найдено
                    searchCountText.setText(R.string.ne_naideno);
                }
            }
        });

        // Обработчик кнопки закрытия поиска
        searchCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearMatches(); // Очищаем найденные тексты
                searchText.setText(""); // Очищаем текст поиска
                mainToolLayout.setVisibility(View.VISIBLE); // Показываем панель инструментов
                searchToolLayout.setVisibility(View.GONE); // Скрываем панель поиска
                hideSoftInput(); // Скрываем клавиатуру
            }
        });

    }

    // Скрываем клавиатуру телефона
    private void hideSoftInput() {
        try {
            // Получаем менеджер ввода
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // Скрываем клавиатуру
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    // Класс собственного загрузчика html
    public class MyViewClient extends WebViewClient {
        // Переопределение метода загрузки страницы в webView
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            urlText.setText(url); // Отображаем адрес страницы
            view.loadUrl(url); // Загружаем страницу
            CookieManager.getInstance().setAcceptCookie(true); // Разрешаем работу с куками
            return true;
        }

        // Переопределение метода окончания загрузки страницы
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            urlText.setText(webView.getUrl()); // Отображаем адрес страницы
        }

        // Переопределение метода начала загрузки страницы
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true); // Включаем поддержку JavaScript
            super.onPageStarted(view, url, favicon); // Вызываем метод начала загрузки страницы
        }

    }

    // Обработчик системной кнопки назад
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) { // Если можно вернуться назад
            webView.goBack(); // Возвращаемся назад
        } else {
            super.onBackPressed(); // Иначе вызываем метод нажатия кнопки назад
        }
    }

    // Обработчик записи состояния активности перед поворотом
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState); // Вызываем метод сохранения состояния активности
        webView.saveState(outState); // Сохраняем состояние webView
    }

}
