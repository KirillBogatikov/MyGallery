package org.kllbff.mygallery;

import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

/**
 * Поток для выполнения задач взаимодействия с Сетью
 * <p>Класс построен по шаблону Singleton</p>
 */
public class NetworkThread extends Thread {
    private static NetworkThread instance;

    /**
     * Возвращает текущий экземпляр класса
     * <p>Метод, в случае первого вызова, может значительно замедлить исполнение потока, в котором
     *    он был вызван по причине необходимости заблокировать исполнение родительского потока до
     *    того момента, когда будет получен {@link Handler} для данного потока</p>
     *
     * @return текущий экземпляр класса
     */
    public static NetworkThread getInstance() {
        if(instance == null) {
            instance = new NetworkThread();
            instance.start();

            //костыльная блокировка
            while(instance.handler == null) {
                //wait
            }
        }
        return instance;
    }

    private NetworkThread() {}

    private Handler handler;

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        Looper.loop();
    }

    /**
     * Позволяет получить экземпляр {@link Handler}, связанный с данным потоком
     *
     * @return экземпляр {@link Handler}, связанный с данным потоком
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * Позволяет добавить Runnable в очередь на исполнение в данном потоке
     *
     * @param runnable исполняемый экземпляр Runnable
     */
    public void execute(Runnable runnable) {
        handler.post(runnable);
    }
}
