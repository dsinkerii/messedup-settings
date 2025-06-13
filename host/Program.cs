using Avalonia;
using Avalonia.ReactiveUI;
using System;

namespace host;
sealed class Program{
    [STAThread]
    public static void Main(string[] args){
        
        CustomLogger.Initialize();
        try
        {
            BuildAvaloniaApp()
            .StartWithClassicDesktopLifetime(args);
        }
        catch (Exception ex)
        {
            CustomLogger.LogException(ex);
            throw;
        }
    }

    // Avalonia configuration, don't remove; also used by visual designer.
    public static AppBuilder BuildAvaloniaApp()
        => AppBuilder.Configure<App>()
            .UsePlatformDetect()
            .WithInterFont()
            .LogToTrace()
            .UseReactiveUI();
}
