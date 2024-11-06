package com.tiltovstan;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.time.LocalTime;

public class VoiceAssistant extends JFrame {
    private LiveSpeechRecognizer recognizer;
    private Voice voice;
    private JTextArea textArea;
    private JButton textButton;
    private JButton voiceButton;

    public VoiceAssistant() {
        // Set up GUI
        setTitle("Voice Assistant");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        textButton = new JButton("Text Command");
        voiceButton = new JButton("Voice Command");

        buttonPanel.add(textButton);
        buttonPanel.add(voiceButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize Speech Recognition (CMU Sphinx)
        try {
            Configuration configuration = new Configuration();
            configuration.setAcousticModelPath("src/main/resources/en-us");
            configuration.setDictionaryPath("src/main/resources/cmudict-en-us.dict");
            configuration.setLanguageModelPath("src/main/resources/en-us/en-us.lm.bin");

            recognizer = new LiveSpeechRecognizer(configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize Text-to-Speech (FreeTTS)
        VoiceManager vm = VoiceManager.getInstance();
        voice = vm.getVoice("kevin16"); // Or another available voice
        if (voice != null) {
            voice.allocate();
        } else {
            System.out.println("Voice not found.");
        }

        // Button Actions
        textButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = JOptionPane.showInputDialog("Enter Command:");
                if (command != null) {
                    processCommand(command);
                }
            }
        });

        voiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listenAndRespond();
            }
        });

        setVisible(true);
    }

    public void listenAndRespond() {
        recognizer.startRecognition(true);
        textArea.append("Listening...\n");

        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            String command = result.getHypothesis();
            textArea.append("You said: " + command + "\n");
            processCommand(command);
        }

        recognizer.stopRecognition();
    }

    public void processCommand(String command) {
        if (command.contains("open browser")) {
            openBrowser();
            speak("Opening the browser.");
        } else if (command.contains("time")) {
            tellTime();
        } else {
            speak("I'm not sure how to respond to that.");
        }
    }

    public void openBrowser() {
        try {
            Desktop.getDesktop().browse(new URI("http://www.google.com"));
        } catch (Exception e) {
            e.printStackTrace();
            speak("I couldn't open the browser.");
        }
    }

    public void tellTime() {
        LocalTime time = LocalTime.now();
        String timeString = "The time is " + time.getHour() + ":" + String.format("%02d", time.getMinute());
        textArea.append(timeString + "\n");
        speak(timeString);
    }

    public void speak(String text) {
        if (voice != null) {
            voice.speak(text);
        } else {
            System.out.println("Text-to-speech voice not available.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VoiceAssistant::new);
    }
}
