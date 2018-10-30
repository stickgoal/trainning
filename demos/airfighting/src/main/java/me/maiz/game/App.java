package me.maiz.game;

import me.maiz.game.ui.MainFrame;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        try {
            MainFrame mainFrame = new MainFrame();
            mainFrame.load();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
