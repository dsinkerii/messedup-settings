using Avalonia.Data.Converters;
using System;
using System.Globalization;

namespace FloatPrecisionFixer{
    public class precisionFix : IValueConverter{
        //fix for the slider value preview, so that you dont see the float precision error
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture){
            if (value is double doubleValue)
            {
                return doubleValue.ToString("F2");
            }
            return value;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture){
            if (value is string stringValue && double.TryParse(stringValue, out var result)){
                return result;
            }
            return value;
        }
    }
}
