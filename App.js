import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, WebView, TouchableOpacity} from 'react-native';
import { NativeModules, DeviceEventEmitter  } from 'react-native';

var Toast = NativeModules.ToastExample;

export default class App extends Component {
  state = {
    text: "failure"
  }

  constructor(){
    super();
  }

  componentDidMount(){
    Toast.showToast("Hello", Toast.SHORT);
    Toast.startSession();
    DeviceEventEmitter.addListener("EVET_E", ({message}) => (
      console.log("done")
    ));
    // async function getPromise() {
    //   try {
    //     var value = await Toast.startSession();
    
    //     console.log(value);
    //   } catch (e) {
    //     console.error(e);
    //   }
    // }
    
    // getPromise();
  }

  componentWillUnmount() {
    DeviceEventEmitter.removeAllListeners();
  }

  render() {
    return (
      <View style={styles.container}>
        <Text>{this.state.text}</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
