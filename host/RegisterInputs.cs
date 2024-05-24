using System;
using Avalonia.Controls.Primitives;
using Avalonia.Input;
using Avalonia.Controls;
using Avalonia.Interactivity;
public partial class RegisterInputs{

    public class SpecialSlider{
        public string Name;
        public double Multiplier;
        public float Adder;
        public SpecialSlider(string name, double multiplier, float adder){
            Name = name;
            Multiplier = multiplier;
            Adder = adder;
        }
    }
    public static string[] keybinds = new string[]{"key.keyboard.q","key.keyboard.w","key.keyboard.e","key.keyboard.r","key.keyboard.t","key.keyboard.y","key.keyboard.u","key.keyboard.i","key.keyboard.o","key.keyboard.p","key.keyboard.a","key.keyboard.s","key.keyboard.d","key.keyboard.f","key.keyboard.g","key.keyboard.h","key.keyboard.j","key.keyboard.k","key.keyboard.l","key.keyboard.z","key.keyboard.x","key.keyboard.c","key.keyboard.v","key.keyboard.b","key.keyboard.n","key.keyboard.m","key.keyboard.shift","key.keyboard.control","key.keyboard.space","key.mouse.left","key.mouse.right"};
    public static string[] languages = new string[]{"af_za (Afrikaans)","ar_sa (Arabic)", "ast_es (Asturian)", "az_az (Azerbaijani)","ba_ru (Bashkir)", "bar (Bavarian)", "be_by (Belarusian)", "bg_bg (Bulgarian)", "br_fr (Breton)", "brb (Brabantian)","bs_ba (Bosnian)", "ca_es (Catalan)", "cs_cz (Czech)","cy_gb (Welsh)", "da_dk (Danish)", "de_at (Austrian German)", "de_ch (Swiss German)", "de_de (German)", "el_gr (Greek)", "en_au (Australian English)", "en_ca (Canadian English)","en_gb (Bri'ish English)", "en_nz (New Zealand English)", "en_pt (Ahoy english)", "en_ud (True australian english)","en_us (American English)", "enp (Modern English minus borrowed words)", "enws (Early Modern English)", "eo_uy (Esperanto)","es_ar (Argentinian Spanish)", "es_cl (Chilean Spanish)", "es_ec (Ecuadorian Spanish)", "es_es (European Spanish)","es_mx (Mexican Spanish)", "es_uy (Uruguayan Spanish)", "es_ve (Venezuelan Spanish)", "esan (Andalusian)","et_ee (Estonian)", "eu_es (Basque)", "fa_ir (Persian)", "fi_fi (Finnish)", "fil_ph (Filipino)", "fo_fo (Faroese)","fr_ca (Canadian French)", "fr_fr (AAA its french)", "fra_de (Franconian)", "fur_it (Friulian)", "fy_nl (Frisian)","ga_ie (Irish)", "gd_gb (SCOTLAND FOREVER)", "gl_es (Galician)", "haw_us (Hawaiian)", "he_il (Hebrew)","hi_in (Hindi)", "hr_hr (Croatian)", "hu_hu (Hungarian)", "hy_am (Armenian)", "id_id (Indonesian)", "ig_ng (Igbo)","ig_ng (Igbo)", "io_en (Ido)", "is_is (Icelandic)", "isv (Interslavic)", "it_it (Italian)", "ja_jp (japanese)","jbo_en (Lojban)", "ka_ge (Georgian)", "kk_kz (Kazakh)", "kn_in (Kannada)", "ko_kr (Korean)", "ksh (Kölsch/Ripuarian)","kw_gb (Cornish)", "la_la (Latin)", "lb_lu (Luxembourgish)", "li_li (Limburgish)", "lmo (Lombard)", "lol_us (yahiacord language (lolcat))","lt_lt (Lithuanian)", "lv_lv (Latvian)", "lzh (Classical Chinese)", "mk_mk (Macedonian)", "mn_mn (Mongolian)", "ms_my (Malay)","mt_mt (Maltese)", "nah (Nahuatl)", "nds_de (Low German)", "nl_be (Dutch, Flemish)", "nl_nl (Dutch)", "nn_no (Norwegian Nynorsk)","no_no‌ (Norwegian Bokmål)", "oc_fr (Occitan)", "ovd (Elfdalian)", "pl_pl (polish (oh wow))", "pt_br (Brazilian Portuguese)", "pt_pt (European Portuguese)","qya_aa (Quenya (Form of Elvish from LOTR))", "ro_ro (Romanian)", "rpr (Russian (Pre-revolutionary))", "ru_ru (ok)","ry_ua (Rusyn)", "se_no (Northern Sami)", "sk_sk (Slovak)", "sl_si (Slovenian)", "so_so (Somali)", "sq_al (Albanian)","sr_sp (Serbian (Cyrillic))", "sv_se (Swedish)", "sxu (Upper Saxon German)", "szl (Silesian)", "ta_in (Tamil)", "th_th (Thai)", "tl_ph (Tagalog)","tlh_aa (Klingon)", "tok (tik (Toki Pona))", "tr_tr (Turkish)", "tt_ru (Tatar)", "uk_ua (Ukrainian (hell yea))", "val_es (Valencian)","vec_it (Venetian)", "vi_vn (Vietnamese)", "yi_de (Yiddish)", "yo_ng (Yoruba)", "zh_cn (Simplified (China; Mandarin))", "zh_hk (Chinese Traditional (Hong Kong; Mix))","zh_tw (Chinese Traditional (Taiwan; Mandarin))", "zlm_arab (Malay (Jawi))"};
    public static void RegisterInputsFunc(string[] sliders, string[] toggles, string[] keybindsCombos, System.EventHandler<Avalonia.Input.PointerReleasedEventArgs> f,System.EventHandler<RoutedEventArgs> fToggle, System.EventHandler<Avalonia.Controls.SelectionChangedEventArgs> fCombo, host.Views.MainWindow window){
        foreach(string slider in sliders){
            Slider _field = window.FindControl<Slider>(slider);
            _field.AddHandler(InputElement.PointerReleasedEvent, f, RoutingStrategies.Tunnel);
        }

        foreach(string toggle in toggles){
            ToggleButton _field = window.FindControl<ToggleButton>(toggle);
            _field.Click += fToggle;
        }

        foreach(string combo in keybindsCombos){
            ComboBox _field = window.FindControl<ComboBox>(combo);
            if(_field.Name == "language"){
                _field.ItemsSource = languages;
            }
            else{
                _field.ItemsSource = keybinds;
            }
            _field.SelectionChanged += fCombo;
        }
    } 
}