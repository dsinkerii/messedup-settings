using System;
using System.Threading.Tasks;
using HiveMQtt;
using HiveMQtt.Client;
using HiveMQtt.MQTT5.Types;
using Avalonia.Input;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Controls.Primitives;
using DialogHostAvalonia;
using Avalonia.Data.Converters;
using System.Globalization;
namespace host.Views
{
    public partial class MainWindow : Window
    {
        public static string[] SlidersToRegister = {"fov", "mouseSensitivity", "soundCategory_master", "maxFps", "renderDistance", "entityDistanceScaling", "gamma", "fovEffectScale", "damageTiltStrength","guiScale","chatScale",
                                                    "pehkuiDDbase","pehkuiDDwidth", "pehkuiDDheight", "pehkuiDDeye_height", "pehkuiDDhitbox_width", "pehkuiDDhitbox_height", "pehkuiDDjump_height", "pehkuiDDstep_height", "pehkuiDDreach", "pehkuiDDblock_reach", "pehkuiDDentity_reach", "pehkuiDDmining_speed", "pehkuiDDknockback", "pehkuiDDattack", "pehkuiDDdefense", "pehkuiDDhealth"};
        public static string[] TogglesToRegister = {"autoJump","invertYMouse"};
        public static string[] Combos = {"key_keylldotllforward","key_keylldotllback","key_keylldotllleft","key_keylldotllright", "key_keylldotlljump", "key_keylldotllsneak", "key_keylldotllsprint", "key_keylldotlldrop", "key_keylldotllinventory", "key_keylldotllchat", "key_keylldotllswapOffhand", "language"};
        public static RegisterInputs.SpecialSlider[] SlidersWithSpecials = {
            new RegisterInputs.SpecialSlider("fov", 0.025,-70),
            new RegisterInputs.SpecialSlider("mouseSensitivity", 0.005,0),
            new RegisterInputs.SpecialSlider("soundCategory_master", 0.01,0),
            new RegisterInputs.SpecialSlider("entityDistanceScaling", 0.01,0),
            new RegisterInputs.SpecialSlider("gamma", 0.1,0),
            new RegisterInputs.SpecialSlider("fovEffectScale", 0.1,0),
            new RegisterInputs.SpecialSlider("damageTiltStrength", 0.1,0),
            new RegisterInputs.SpecialSlider("chatScale", 0.1,0)
            };
        private TextBox _MqttTextBox;
        private TextBox _NickTextBox;
        private TextBox _PwdTextBox;
        private DialogHost _NoPwdDialog;
        public HiveMQClient mqttClient;
        public MainWindow(){
            InitializeComponent();

            _MqttTextBox = this.FindControl<TextBox>("mqtt");
            _NoPwdDialog = this.FindControl<DialogHost>("mqttnopwd");
            
            var settings = Settings.LoadSettings();

            if (settings.MqttServer != null){
                _MqttTextBox.Text = settings.MqttServer;
            }
            else{
                _MqttTextBox.Text = "mqtt.eclipseprojects.io";
            }

            _NickTextBox = this.FindControl<TextBox>("nickname");
            _PwdTextBox = this.FindControl<TextBox>("password");

            if (settings.Nickname != null){
                _NickTextBox.Text = settings.Nickname;
            }
            else{
                Random rand = new Random();
                _NickTextBox.Text = $"Actor-{rand.Next(1, int.MaxValue)}";
            }

            _MqttTextBox.TextChanged += OnTextBoxTextChanged;
            _NickTextBox.TextChanged += OnTextBoxTextChanged;

            RegisterInputs.RegisterInputsFunc(
                SlidersToRegister, TogglesToRegister, Combos, 
                SendModifiedMqtt, SendModifiedMqttToggle, SendModifiedMqttCombo, this);

            StartMqttConnection(_NickTextBox.Text);
        }

        private async void StartMqttConnection(string name){
            var options = new HiveMQClientOptionsBuilder()
                .WithBroker(_MqttTextBox.Text == null ? "mqtt.eclipseprojects.io" : _MqttTextBox.Text)
                .WithPort(8883)
                .WithUseTls(true)
                .Build();

            Random rand = new Random();
            options.UserName = name;
            options.Password = $"Password-{rand.Next(1, int.MaxValue)}";

            mqttClient = new HiveMQClient(options);

            try{
                var connectResult = await mqttClient.ConnectAsync();
                var builder = new SubscribeOptionsBuilder();
                builder.WithSubscription("1.20settingsmodv1.3", QualityOfService.AtLeastOnceDelivery);
                var subscribeOptions = builder.Build();
                var subscribeResult = await mqttClient.SubscribeAsync(subscribeOptions);
                CustomLogger.Log($"Connected result: {connectResult}\nSubscribe options:{subscribeOptions}\nSubscription result:{subscribeResult}");
            }
            catch (Exception ex){
                CustomLogger.Log($"Connection failed: {ex.Message}");
            }
        }
        public void GetLogs(object sender, Avalonia.Interactivity.RoutedEventArgs e) {
            try 
            {
                System.Diagnostics.Process.Start(new System.Diagnostics.ProcessStartInfo
                {
                    FileName = CustomLogger.LogFilePath,
                    UseShellExecute = true
                });
            }
            catch (Exception ex)
            {
                CustomLogger.Log($"Failed to open log file: {ex.Message}");
            }
        }

        // SLIDERSS!!!
        public void SendModifiedMqtt(object sender, Avalonia.Input.PointerReleasedEventArgs e) {
            CustomLogger.Log($"Sending message...");
            double value = ((Slider) sender).Value;
            
            string newname = ((Slider) sender).Name.Replace("DD","::");
            SendMessage(newname, $"{value}");
        }

        // TOGGLES!!
        public void SendModifiedMqttToggle(object sender, RoutedEventArgs e) {
            CustomLogger.Log($"Sending message...");
            string value = (((ToggleButton) sender).IsChecked.ToString()).ToLower();
            SendMessage($"{((ToggleButton) sender).Name}", value);
        }
        public void SendModifiedMqttCombo(object sender, Avalonia.Controls.SelectionChangedEventArgs e) {
            CustomLogger.Log($"Sending message...");
            string newname = ((ComboBox) sender).Name.Replace("lldotll",".");
            if(newname != "language"){
                string value = RegisterInputs.keybinds[((ComboBox) sender).SelectedIndex];
                SendMessage($"{newname}", value);
            }else{
                string value = RegisterInputs.languages[((ComboBox) sender).SelectedIndex];
                value = value.Split(" ")[0];
                SendMessage($"{newname}", value);
            }
        }

        private async void SendMessage(string from, string content){
            if(_PwdTextBox != null){
                if(_PwdTextBox.Text == null){
                    CustomLogger.Log("No password!!");
                    _NoPwdDialog.IsOpen = true;
                    return;
                }
                string raw = $"{_NickTextBox.Text}:{from}:{content}";
                string encrypted = CryptoMessage.EncryptMessage(raw, _PwdTextBox.Text);

                if(encrypted != null || encrypted != ""){
                    var publishResult = await mqttClient.PublishAsync("1.20settingsmodv1.3", encrypted);
                    CustomLogger.Log($"Message content: {raw} (encrypred: {encrypted})");
                    CustomLogger.Log($"Message status: {publishResult.ReasonCode()}");
                }else{
                    CustomLogger.Log("bad password!!");
                    _NoPwdDialog.IsOpen = true;
                }
            }
        }
        private void OnTextBoxTextChanged(object sender, RoutedEventArgs e){
            var textBox = sender as TextBox;
            if (textBox != null)
            {
                if (textBox.Name == "mqtt")
                    Settings.SaveMqttNameSettings(textBox.Text);
                else
                    Settings.SaveNickNameSettings(textBox.Text);
            }
        }
    }

}
