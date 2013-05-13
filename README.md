# postgres-lingrbot

A Lingr Bot to run arbitrary SQL statements on PostgreSQL. Written in Clojure.

## Usage

* GET http://postgres-lingrbot.herokuapp.com/
    * meta info
* POST http://postgres-lingrbot.herokuapp.com/
    * (with lingr bot post)
    * message has to begin with a capital letter and end with semicolon
    * the bot *executes* it.
* POST http://postgres-lingrbot.herokuapp.com/direct
    * e.g. curl -d 'Select * from hello;' http://postgres-lingrbot.herokuapp.com/direct?test=verytest

## License

Copyright (c) 2013 Tatsuhiro Ujihisa

GPLv3
