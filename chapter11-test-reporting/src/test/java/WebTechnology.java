public enum WebTechnology {
    BACKBONEJS("Backbone.js"),
    ANGULARJS("AngularJS"),
    REACT("React"),
    VUEJS("Vue.js"),
    CANJS("CanJS"),
    EMBERJS("Ember.js"),
    KNOCKOUTJS("KnockoutJS"),
    MARIONETTEJS("Marionette.js"),
    POLYMER("Polymer"),
    ANGULAR2("Angular 2.0"),
    DART("Dart"),
    ELM("Elm"),
    CLOSURE("Closure"),
    VANILLAJS("Vanilla JS"),
    JQUERY("jQuery"),
    CUJOJS("cujoJS"),
    SPINE("Spine"),
    DOJO("Dojo"),
    MITHRIL("Mithril"),
    KOTLIN_REACT("Kotlin + React"),
    FIREBASE_ANGULARJS("Firebase + AngularJS"),
    VANILLA_ES6("Vanilla ES6");

    private String technologyName;


    WebTechnology(String technologyName) {
        this.technologyName = technologyName;
    }

    public String getTechnologyName() {
        return technologyName;
    }
}
