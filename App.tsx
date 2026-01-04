import { useEffect, useState } from 'react';
import { StatusBar, StyleSheet, Text, useColorScheme, View } from 'react-native';
import { Button } from 'react-native';
import { NativeModules } from 'react-native';
import { requestPermissions } from './permissions';
import { getData, removeData, storeData } from './Telephony';
const { TelephonyModule } = NativeModules;

function App() {
  const isDarkMode = useColorScheme() === 'dark';
  const [my_key, setMyKey] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      const granted = await requestPermissions();
      if (!granted) console.warn('Permissions not granted!');
    })();

    (async () => {
      const data = await getData();
      setMyKey(data);
    })();
  }, []);

  const startSocketService = async () => {
    //const url = 'http://192.168.31.228:5000';
    const url = 'https://api.jhotpotpay.com';
    await storeData(url);
    setMyKey(url); // ✅ update state
    TelephonyModule.startSocketService(url);
  };

  const stopSocketService = async () => {
    await removeData();
    setMyKey(null); // ✅ update state
    TelephonyModule.stopSocketService();
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />

      <Button
        disabled={!!my_key}
        title="Start Socket Service"
        onPress={startSocketService}
      />

      <Button
        disabled={!my_key}
        title="Stop Socket Service"
        onPress={stopSocketService}
      />

      <Text style={{ fontSize: 18 }}>Phone & Message Controller</Text>
      <Text>Powered by ScientistX Technology</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#fff",
    gap: 10
  },
});

export default App;
