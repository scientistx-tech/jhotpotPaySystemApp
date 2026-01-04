import { request, PERMISSIONS, RESULTS } from 'react-native-permissions';

export async function requestPermissions() {
  const phone = await request(PERMISSIONS.ANDROID.READ_PHONE_STATE);
  const sms = await request(PERMISSIONS.ANDROID.SEND_SMS);
  const call = await request(PERMISSIONS.ANDROID.CALL_PHONE);
  return phone === RESULTS.GRANTED && sms === RESULTS.GRANTED && call === RESULTS.GRANTED;
}