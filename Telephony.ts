import AsyncStorage from '@react-native-async-storage/async-storage';
import { Alert } from 'react-native';

export const storeData = async (value: string) => {
  try {
    await AsyncStorage.setItem('my-key', value);
  } catch (e: any) {
    // saving error
    Alert.alert(e?.message);
  }
};
export const getData = async () => {
  try {
    const value = await AsyncStorage.getItem('my-key');
    if (value !== null) {
      // value previously stored
      return value;
    }
    return null;
  } catch (e: any) {
    // error reading value
    console.warn(e?.message);
    return null;
  }
};
export const removeData = async () => {
  try {
    await AsyncStorage.removeItem('my-key');
  } catch (e: any) {
    // error reading value
    Alert.alert(e?.message);
  }
};
