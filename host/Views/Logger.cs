using System;
using System.IO;
using System.Text;

public static class CustomLogger
{
    public static readonly string LogFilePath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, ".host_logs.txt");
    private static int _frameCount = 0;
    private static bool _isInitialized = false;

    public enum LogType
    {
        Error,
        Exception,
        Warning,
        Log
    }

    public static void Initialize()
    {
        if (_isInitialized) return;
        
        _isInitialized = true;

        if (File.Exists(LogFilePath))
        {
            File.Delete(LogFilePath);
        }

        Console.SetOut(new LoggingTextWriter(Console.Out));
        Console.SetError(new LoggingTextWriter(Console.Error, LogType.Error));
    }

    public static void Log(string message, LogType type = LogType.Log, string stackTrace = null)
    {
        _frameCount++;
        string formattedMessage = FormatMessage(type.ToString(), message, stackTrace);
        SaveLogToFile(formattedMessage);
    }

    public static void LogError(string message, string stackTrace = null)
    {
        Log(message, LogType.Error, stackTrace);
    }

    public static void LogWarning(string message, string stackTrace = null)
    {
        Log(message, LogType.Warning, stackTrace);
    }

    public static void LogException(Exception ex)
    {
        Log(ex.Message, LogType.Exception, ex.StackTrace);
    }

    private static string FormatMessage(string logType, string message, string stackTrace)
    {
        string time = DateTime.Now.ToString("HH:mm:ss");
        int frame = _frameCount;

        string hierarchy = GenerateCallHierarchy(stackTrace);

        StringBuilder logMessage = new StringBuilder();
        logMessage.AppendLine($"[{time}, frame {frame}] -- {logType} -- {{");
        logMessage.AppendLine($"      - {logType} message: \"{message}\"");
        logMessage.AppendLine($"      - {logType} path:");
        logMessage.Append(hierarchy);
        logMessage.AppendLine("}");

        return logMessage.ToString();
    }

    private static string GenerateCallHierarchy(string stackTrace)
    {
        if (string.IsNullOrEmpty(stackTrace))
        {
            return "> No stack trace available.\n";
        }

        StringBuilder hierarchy = new StringBuilder();
        string[] stackLines = stackTrace.Split(new[] { '\n' }, StringSplitOptions.RemoveEmptyEntries);

        for (int i = 0; i < stackLines.Length; i++)
        {
            string line = stackLines[i].Trim();
            string amountOfDashes = new string('-', i);
            hierarchy.AppendLine($"{amountOfDashes}> {line}");
        }

        return hierarchy.ToString();
    }

    private static void SaveLogToFile(string message)
    {
        try
        {
            using (StreamWriter writer = new StreamWriter(LogFilePath, true))
            {
                writer.WriteLine(message);
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Failed to write to log file: {ex.Message}");
        }
    }

    private class LoggingTextWriter : TextWriter
    {
        private readonly TextWriter _originalWriter;
        private readonly LogType _logType;

        public LoggingTextWriter(TextWriter originalWriter, LogType logType = LogType.Log)
        {
            _originalWriter = originalWriter;
            _logType = logType;
        }

        public override Encoding Encoding => _originalWriter.Encoding;

        public override void WriteLine(string value)
        {
            _originalWriter.WriteLine(value);
            if (!string.IsNullOrEmpty(value))
            {
                Log(value, _logType, Environment.StackTrace);
            }
        }

        public override void Write(char value)
        {
            _originalWriter.Write(value);
        }

        public override void Write(string value)
        {
            _originalWriter.Write(value);
        }
    }
}
