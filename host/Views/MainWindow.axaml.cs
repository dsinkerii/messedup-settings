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
namespace host.Views {
    public partial class MainWindow : Window {
        public static string[] SlidersToRegister = {"fov", "mouseSensitivity", "soundCategory_master", "maxFps", "renderDistance", "entityDistanceScaling", "gamma", "fovEffectScale", "damageTiltStrength","guiScale","chatScale",
                                                    "pehkuiDDbase","pehkuiDDwidth", "pehkuiDDheight", "pehkuiDDeye_height", "pehkuiDDhitbox_width", "pehkuiDDhitbox_height", "pehkuiDDjump_height", "pehkuiDDstep_height", "pehkuiDDreach",
                                                    "pehkuiDDblock_reach", "pehkuiDDentity_reach", "pehkuiDDmining_speed", "pehkuiDDknockback", "pehkuiDDattack", "pehkuiDDdefense", "pehkuiDDhealth", "pehkuiDDview_bobbing", "pehkuiDDmotion"};
        public static string[] TogglesToRegister = { "autoJump", "invertYMouse" };
        public static string[] Combos = { "key_keylldotllforward", "key_keylldotllback", "key_keylldotllleft", "key_keylldotllright", "key_keylldotlljump", "key_keylldotllsneak", "key_keylldotllsprint", "key_keylldotlldrop", "key_keylldotllinventory", "key_keylldotllchat", "key_keylldotllswapOffhand", "language" };
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
        public string MqttConnText;
        private string currentMqttServer = "broker.emqx.io";
        public MainWindow() {
            InitializeComponent();

            _MqttTextBox = this.FindControl<TextBox>("mqtt");
            _NoPwdDialog = this.FindControl<DialogHost>("mqttnopwd");
            
            var settings = Settings.LoadSettings();
            currentMqttServer = settings.MqttServer ?? "broker.emqx.io";
            
            var serverSelector = this.FindControl<ComboBox>("mqttServerSelector");
            bool isPreset = false;
            for(int i = 0; i < serverSelector.Items.Count - 1; i++){
                var item = serverSelector.Items[i] as ComboBoxItem;
                if(item?.Tag?.ToString() == currentMqttServer){
                    serverSelector.SelectedIndex = i;
                    isPreset = true;
                    break;
                }
            }
            
            if(!isPreset){
                serverSelector.SelectedIndex = serverSelector.Items.Count - 1; // "Custom..."
                _MqttTextBox.Text = currentMqttServer;
                _MqttTextBox.IsVisible = true;
            }

            _NickTextBox = this.FindControl<TextBox>("nickname");
            _PwdTextBox = this.FindControl<TextBox>("password");

            if (settings.Nickname != null) {
                _NickTextBox.Text = settings.Nickname;
            }
            else {
                Random rand = new Random();
                _NickTextBox.Text = $"Actor-{rand.Next(1, int.MaxValue)}";
            }
            Console.WriteLine($"App launched, with such settings: actor: {_NickTextBox.Text}; server: {_MqttTextBox.Text}");

            _MqttTextBox.TextChanged += OnTextBoxTextChanged;
            _NickTextBox.TextChanged += OnTextBoxTextChanged;

            RegisterInputs.RegisterInputsFunc(
                SlidersToRegister, TogglesToRegister, Combos,
                SendModifiedMqtt, SendModifiedMqttToggle, SendModifiedMqttCombo, this);

            StartMqttConnection(_NickTextBox.Text);
        }

        private async Task StartMqttConnection(string name) {
            var options = new HiveMQClientOptionsBuilder()
                .WithBroker(currentMqttServer)
                .WithPort(8883)
                .WithUseTls(true)
                .Build();

            Random rand = new Random();
            options.UserName = name;
            options.Password = $"Password-{rand.Next(1, int.MaxValue)}";

            mqttClient = new HiveMQClient(options);

            try {
                var connectResult = await mqttClient.ConnectAsync();
                var builder = new SubscribeOptionsBuilder();
                builder.WithSubscription("1.20settingsmodv1.3", QualityOfService.AtLeastOnceDelivery);
                var subscribeOptions = builder.Build();
                var subscribeResult = await mqttClient.SubscribeAsync(subscribeOptions);
                Console.WriteLine($"Connected result: {connectResult}...");

                await Avalonia.Threading.Dispatcher.UIThread.InvokeAsync(() => {
                    var statusBlock = this.FindControl<TextBlock>("mqttConnStat");
                    statusBlock.Text = "✅ MQTT server:";
                });
            }
            catch (Exception ex) {
                Console.WriteLine($"Connection failed: {ex.Message}");

                await Avalonia.Threading.Dispatcher.UIThread.InvokeAsync(() => {
                    var statusBlock = this.FindControl<TextBlock>("mqttConnStat");
                    statusBlock.Text = "❌ MQTT server:";
                });
            }
        }
        private void OnMqttServerChanged(object sender, SelectionChangedEventArgs e) {
            var combo = sender as ComboBox;
            var selected = combo.SelectedItem as ComboBoxItem;

            if (selected?.Tag?.ToString() == "custom" && _MqttTextBox != null) {
                _MqttTextBox.IsVisible = true;
                currentMqttServer = _MqttTextBox.Text ?? "broker.emqx.io";
            }
            else if(_MqttTextBox != null){
                _MqttTextBox.IsVisible = false;
                currentMqttServer = selected?.Tag?.ToString() ?? "broker.emqx.io";
                Settings.SaveMqttNameSettings(currentMqttServer);
            }
        }
        public void GetLogs(object sender, Avalonia.Interactivity.RoutedEventArgs e) {
            try {
                System.Diagnostics.Process.Start(new System.Diagnostics.ProcessStartInfo {
                    FileName = CustomLogger.LogFilePath,
                    UseShellExecute = true
                });
            }
            catch (Exception ex) {
                Console.WriteLine($"Failed to open log file: {ex.Message}");
            }
        }

        // SLIDERSS!!!
        public void SendModifiedMqtt(object sender, Avalonia.Input.PointerReleasedEventArgs e) {
            Console.WriteLine($"Sending message...");
            double value = ((Slider)sender).Value;

            string newname = ((Slider)sender).Name.Replace("DD", "::");
            SendMessage(newname, $"{value}");
        }

        // TOGGLES!!
        public void SendModifiedMqttToggle(object sender, RoutedEventArgs e) {
            Console.WriteLine($"Sending message...");
            string value = (((ToggleButton)sender).IsChecked.ToString()).ToLower();
            SendMessage($"{((ToggleButton)sender).Name}", value);
        }
        public void SendModifiedMqttCombo(object sender, Avalonia.Controls.SelectionChangedEventArgs e) {
            Console.WriteLine($"Sending message...");
            string newname = ((ComboBox)sender).Name.Replace("lldotll", ".");
            if (newname != "language") {
                string value = RegisterInputs.keybinds[((ComboBox)sender).SelectedIndex];
                SendMessage($"{newname}", value);
            }
            else {
                string value = RegisterInputs.languages[((ComboBox)sender).SelectedIndex];
                value = value.Split(" ")[0];
                SendMessage($"{newname}", value);
            }
        }

        private async void SendMessage(string from, string content) {
            try {
                if (_PwdTextBox?.Text == null || string.IsNullOrEmpty(_PwdTextBox.Text)) {
                    Console.WriteLine("no password!!");
                    await ShowErrorDialog("MQTT password invalid!", "password is empty or incorrect");
                    return;
                }

                if (mqttClient == null || !mqttClient.IsConnected()) {
                    Console.WriteLine("not connected to MQTT server!");
                    await ShowErrorDialog("connection error", "not connected to MQTT server. please reconnect.");
                    return;
                }

                string raw = $"{_NickTextBox.Text}:{from}:{content}";
                string encrypted = CryptoMessage.EncryptMessage(raw, _PwdTextBox.Text);

                if (string.IsNullOrEmpty(encrypted)) {
                    Console.WriteLine("encryption failed!");
                    await ShowErrorDialog("encryption error", "failed to encrypt message. check your password.");
                    return;
                }

                var publishResult = await mqttClient.PublishAsync("1.20settingsmodv1.3", encrypted);
                Console.WriteLine($"message content: {raw} (encrypted: {encrypted})");
                Console.WriteLine($"message status: {publishResult.ReasonCode()}");

            }
            catch (Exception ex) {
                Console.WriteLine($"failed to send message: {ex.Message}");
                await ShowErrorDialog("send Failed", $"error: {ex.Message}");
            }
        }
        private async void ReconnectToMqtt(object sender, RoutedEventArgs e){
            var button = sender as Button;
            button.IsEnabled = false;
            button.Content = "connecting...";
            
            try {
                if(mqttClient != null && mqttClient.IsConnected()){
                    await mqttClient.DisconnectAsync();
                }
                
                await StartMqttConnection(_NickTextBox.Text);
                if (mqttClient != null && mqttClient.IsConnected()){
                    button.Content = "connected!";
                    await Task.Delay(2000);
                }
                else {
                    CustomLogger.Log($"reconnect failed...");
                    await ShowErrorDialog("connection Failed", $"could not connect...");
                    button.Content = "failed";
                    await Task.Delay(2000);
                }
                
            } catch (Exception ex) {
                CustomLogger.Log($"reconnect failed: {ex.Message}");
                await ShowErrorDialog("connection Failed", $"could not connect: {ex.Message}");
                button.Content = "failed";
                await Task.Delay(2000);
            } finally {
                button.Content = "reconnect";
                button.IsEnabled = true;
            }
        }
        private void OnCustomMqttTextChanged(object sender, TextChangedEventArgs e){
            var textBox = sender as TextBox;
            if (!string.IsNullOrEmpty(textBox?.Text)){
                currentMqttServer = textBox.Text;
                Settings.SaveMqttNameSettings(currentMqttServer);
            }
        }
        private async Task ShowErrorDialog(string title, string message) {
            await Avalonia.Threading.Dispatcher.UIThread.InvokeAsync(() => {
                var dialog = this.FindControl<DialogHost>("mqttnopwd");
                var stack = dialog.DialogContent as StackPanel;
                if (stack != null && stack.Children.Count >= 2) {
                    (stack.Children[0] as TextBlock).Text = title;
                    (stack.Children[1] as TextBlock).Text = message;
                }
                dialog.IsOpen = true;
            });
        }
        private void OnTextBoxTextChanged(object sender, RoutedEventArgs e) {
            var textBox = sender as TextBox;
            if (textBox != null) {
                if (textBox.Name == "mqtt")
                    Settings.SaveMqttNameSettings(textBox.Text);
                else
                    Settings.SaveNickNameSettings(textBox.Text);
            }
        }
    }

}