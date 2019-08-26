package com.werti.simonsayssomething.BukkitRunnables;

import com.werti.Stdafx;
import com.werti.simonsayssomething.SimonGame;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer
{
  private SimonGame simonGame;
  private Action action;
  private int timeInSeconds;
  private int timeInSecondsCounter;
  private boolean running = false;

  public Timer(SimonGame simonGame, Action action, int timeInSeconds)
  {
    this.simonGame = simonGame;
    this.action = action;
    this.timeInSeconds = timeInSeconds;
  }

  public void execute()
  {
    if (running)
    {
      Stdafx.plugin.getLogger().severe("Timer is still running!"); //TODO: Add ID to SimonGame
    }

    running = true;

    timeInSecondsCounter = timeInSeconds;

    run();
  }

  private void run()
  {
    Stdafx.bukkitScheduler.runTaskLater(Stdafx.plugin, new BukkitRunnable()
    {
      @Override
      public void run()
      {
        if (timeInSecondsCounter == 0)
        {
          switch (action)
          {
            case StartGame:
              simonGame.startSequenceAfterTimer();
              break;
          }
          running = false;
        }
        else
        {
          simonGame.broadcast(Stdafx.highlightColor + timeInSecondsCounter + Stdafx.textColor + "...");
          timeInSecondsCounter--;
          Timer.this.run();
        }
      }
    }, Stdafx.Tickrate);
  }

  public enum Action
  {
    StartGame
  }
}
