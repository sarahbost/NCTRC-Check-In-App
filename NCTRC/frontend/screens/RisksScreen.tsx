import React, { useState } from 'react';
import { SafeAreaView, ScrollView, TextInput, Text, View, Button, Image, TouchableOpacity, KeyboardAvoidingView } from 'react-native';
import { StackNavigationProp } from '@react-navigation/stack';
import { AuthStackParamList, AppScreens } from '../index';
import { components } from '../domain/domain';
import { createAndSigninUser } from '../handler/handlers';
import { styles } from './Styles';
import Signature from 'react-native-signature-canvas';

type RisksScreenNavigationProps = StackNavigationProp<AuthStackParamList, AppScreens.Risks>;

interface RisksScreenProps {
    route: { params: RisksParams };
    navigation: RisksScreenNavigationProps;
}
export type RisksParams = {
    firstName: string;
    lastName: string; 
    email: string;
    tempurature: number;
    visitorType: string; 
};

const RisksScreen: React.FunctionComponent<RisksScreenProps> = (props) => {
    let { navigation, route } = props;
    let { params } = route;
    let { firstName, lastName, email, tempurature, visitorType } = params;
    let [signature, setSign] = useState(''); 
    let [error, setError] = useState(false); 
    let [errorMessage, setErrorMessage] = useState(""); 

    
    return (
        <ScrollView style={styles.scrollContainer}>
            <KeyboardAvoidingView behavior={"padding"}>
             <TouchableOpacity style={styles.backButton} onPress={() => navigation.pop()}>
                     <Image source={require('./../assets/backbutton.png')} style={{ width: 75, height: 75 }}></Image>
                </TouchableOpacity> 
                <TouchableOpacity style={styles.logo} onPress={() => navigation.popToTop()}>
                     <Image source={require('./../assets/NCTRClogo.png')} style={{ width: 150, height: 150 }}></Image>
                </TouchableOpacity> 
            <View style={styles.homeContainer}></View>
            <View>
                <Text style={styles.riskTitle}>COVID-19 Acknowledgement of Risk and Acceptance of Services</Text>
                <Text style={styles.riskDoc}>I am aware of the risks of contracting or spreading Covid-19 while working or volunteering at NC Therapeutic Riding Center; attending an event; and/or receiving face-to-face services from NC Therapeutic Riding Center during the time of a pandemic outbreak, and /or North Carolina Governor’s or Orange County’s declaration of a “stay-at-home” order(s). </Text>
                <Text style={styles.riskDoc}>I am aware that face-to-face services and experiences increase my risk of contracting and passing on the Covid-19 or Coronavirus and agree to hold harmless NC Therapeutic Riding Center and its residents, members, officers, managers, agents, employees and all other individuals I may come in contact with during this interaction and receiving of services, providing services, attending an event or volunteering within this organization. I am aware of the options that may be available for remote services including, telephonic and video telehealth, as allowed by insurances and State Licensing Board recommendations, during this Pandemic outbreak.</Text>
                <Text style={styles.riskDoc}>I agree to and will follow all guidelines for personal hygiene, personal safety and public safety as recommended by NC Therapeutic Riding Center; as well as my individual provider/practitioner. This may include, but is not limited to, waiting in my vehicle and/or home until I am asked to enter the building/farm; maintaining social distance; washing my hands prior to and following each session or activity; use of hand sanitizer upon request; wiping down surfaces with disinfecting wipes and/or wearing a protective medical mask and/or gloves.</Text>
                <Text style={styles.riskDoc}>I agree to stay home and/or cancel my services should I have personally exhibited or have been in contact with someone who has presented with illness within the previous 24 hours to 2 weeks, including; cough, sneezing, fever, chest congestion or additional signs of potential spread of any virus or bacteria/disease. In addition, I will follow the recommendations of my provider once I have notified them of these risks in regards to my future services or attendance during this pandemic.</Text>
                <Text style={styles.riskDoc}>NC Therapeutic Riding Center will engage in regular cleaning and sanitizing of the facility, horse tack, grooming supplies and office, doors, and frequently touched areas in-between clients and on a daily basis as recommended by the CDC for the safety of clients, employees, volunteers and horses.</Text>
                <Text style={styles.riskDoc}>I am signing under my own free will and agree to follow these and hold harmless all individuals associated with or through my services acquired from NC Therapeutic Riding Center.</Text>
                <Text style={styles.riskDoc}>By typing my name below, I CONFIRM THAT I HAVE READ AND UNDERSTAND THIS DOCUMENT.</Text>
                <TextInput style={styles.textInput} onChangeText={(text) => setSign(text)} placeholder="Please type your name to confirm" />
                <Text style={styles.riskDoc}>*In the event that the undersigned is under the age of 18, the signature of a parent or guardian is required.</Text>
                {error && <Text style={styles.errorMessage}>{errorMessage}</Text>}
                <TouchableOpacity style={styles.smallButton}
                    onPress={() => {
                        if(signature ==='') {
                            setErrorMessage("Please enter your name.")
                            setError(true);
                        } else {
                        let newUser: components["schemas"]["NewUserRequestModel"] = {
                            user: {
                              firstName: firstName,
                              lastName: lastName,
                              email: email,
                            },
                            signinData: { temperature: tempurature, visitorType: visitorType },
                            signature: signature,
                          };
                        createAndSigninUser(newUser).then(
                            (response) => {
                                console.log(response); 
                                if(response ==200 || response == 201) {
                                  navigation.navigate(AppScreens.SignInLanding);
                                } else if (response == 409) {
                                    navigation.navigate(AppScreens.CovidError, {reason: "the center is currently at maximum capacity."});                   
                                } else {
                                  navigation.navigate(AppScreens.CovidError, {reason: "we are unable to check you in at this time."});
                                }
                            }); 
                    }}}
                    >
                    
                    <Text style={styles.buttonText}>
                    Submit
                     </Text>  
                </TouchableOpacity>
            </View>
            </KeyboardAvoidingView>

        </ScrollView>
    );
};
export default RisksScreen;
