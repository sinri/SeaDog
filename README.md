# SeaDog

![Maven Central](https://img.shields.io/maven-central/v/io.github.sinri/SeaDog)
![GitHub](https://img.shields.io/github/license/sinri/SeaDog)

A framework targeting for JAVA 11 on Aliyun FC, with Keel eco.

## Quick Start

### Step 1

In your FC project, add dependency:

```xml
<dependency>
    <groupId>io.github.sinri</groupId>
    <artifactId>SeaDog</artifactId>
</dependency>
```

### Step 2

Implement your own delegate for your business on Aliyun FC.
Just define a class extending `io.github.sinri.drydock.naval.raider.seadog.AbstractSeaDogDelegate`.
Let's call it the `DELEGATE` class.

### Step 3

Implement your own executor to handle the FC requirements.
Now define a class extending `io.github.sinri.drydock.naval.raider.seadog.SeaDog`, let's call it the `EXECUTOR` class.
Note that you should map the `DELEGATE` class to the `EXECUTOR` class;
implement the method `createDelegate` in the `EXECUTOR` class to create an instance of the `DELEGATE` class.

### Step 4

Package your project and upload the JAR file to Aliyun FC, 
and configure the requirement receiver as the `handleRequest` method of your `EXECUTOR` class.

### Step 5

Run your FC! 