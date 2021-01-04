# Melmac

A prototype of a combined writing/citation-collection desktop application,
written with clojurescript, running on electron.

## Installation

```
$ npm install -g electron # if already installed
$ cp src/cljs/melmac/config.cljs.template src/cljs/melmac/config.cljs # edit the store-path then (absolute, with ending slash)
```

## Start

on one terminal:

```
lein figwheel dev
```

on another terminal:

```
npm start
```

## Test

```
lein doo node test once
```

## Usage

```
\d as page number to delete a citation
```

## Historical Note

* bootstrapped with https://github.com/Gonzih/cljs-electron
