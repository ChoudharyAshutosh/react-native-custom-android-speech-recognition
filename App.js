import React,{useState, useEffect} from "react";
import { SafeAreaView, Button, Text, View } from "react-native";
import SpeechRecognition from './src/AppModules/SpeechRecognition';
import NetInfo from "@react-native-community/netinfo";
import {request, PERMISSIONS, RESULTS} from 'react-native-permissions';

const App = ()=>{
  const [str, setStr] = useState('');
  const [qtyStr, setQtyStr] = useState('');
  const [isListening, setIsListening] = useState(false);

  useEffect(()=>{
    // Subscribe
    const unsubscribe = NetInfo.addEventListener(state => {
      console.log("Connection type", state.type);
      console.log("Is connected?", state.isConnected);
      if(!state.isConnected){
        alert("Please connect to internet.");
      }
    });

    return ()=>{
      // Unsubscribe
      unsubscribe();
    }
  },[])

  const listenStr = async() => {
    request(PERMISSIONS.ANDROID.RECORD_AUDIO)
    .then((result) => {
        if(result == RESULTS.GRANTED){
          setIsListening(true);
          SpeechRecognition.startListening()
          .then((value)=>{
            console.log(value);
            setStr(value);
            setIsListening(false);
          })
          .catch((error)=>{
              let str = error.message.split(',');
              console.log(str[0]);
              setStr(str[1]);
              setIsListening(false);
              alert(str[0]);
          })
        }
        else{
          alert('Record audio permission not granted.');
        }
    })
    .catch((error)=>{
      alert(JSON.stringify(error));
    });
  };

  const listenQtyStr = async() => {
    request(PERMISSIONS.ANDROID.RECORD_AUDIO)
    .then((result) => {
        if(result == RESULTS.GRANTED){
          setIsListening(true);
          SpeechRecognition.startListening()
          .then((value)=>{
            console.log(value);
            setQtyStr(value);
            setIsListening(false);
          })
          .catch((error)=>{
              let str = error.message.split(',');
              console.log(str[0]);
              setQtyStr(str[1]);
              setIsListening(false);
              alert(str[0]);
          })
        }
        else{
          alert('Record audio permission not granted.');
        }
    })
    .catch((error)=>{
      alert(JSON.stringify(error));
    });
  }

  return(
    <SafeAreaView style={{flex:1,justifyContent:'flex-start',alignItems:'center',marginTop:20}}>
      <View style={{marginBottom:30, marginTop:15}}>
        <Text style={{fontSize:20,color:'green',backgroundColor:'white',paddingHorizontal:20,paddingVertical:10,borderRadius:10,elevation:10}}>
          Speech Recognition
        </Text>
      </View>
      {
        isListening && 
        (
          <View>
            <Text style={{bottom:10, color:'gray'}}>
              Listening ...
            </Text>
          </View>
        )
      }
      <View style={{marginBottom:20,padding:20,width:'100%'}}>
        <View style={{marginBottom:10}}>
          <Text style={{color:'blue',backgroundColor:'lightgrey',padding:10,borderRadius:5,width:'100%'}}>{str}</Text>
        </View>
        <Button
          title="Record string"
          color="#841584"
          onPress={listenStr}
        />
      </View>
      <View style={{padding:20,width:'100%'}}>
        <View style={{marginBottom:10}}>
          <Text style={{color:'blue',backgroundColor:'lightgrey',padding:10,borderRadius:5,width:'100%'}}>{qtyStr}</Text>
        </View>
        <Button
          title="Record quantity"
          color="#841584"
          onPress={listenQtyStr}
        />
      </View>
    </SafeAreaView>
  )
}

export default App;