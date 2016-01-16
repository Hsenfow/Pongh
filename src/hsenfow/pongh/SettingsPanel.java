package hsenfow.pongh;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hsenfow.pongh.Settings.Setting;
import hsenfow.pongh.Settings.SettingInfo;

public class SettingsPanel extends JPanel{
	private static final long serialVersionUID = -3977716726385898691L;
	
	// The current fields of each type. These are used to extract all the values when the settings
	// screen is left.
	private Stack<JTextField> textFields;
	private Stack<JComboBox<String>> selectFields;
	private Stack<JCheckBox> checkboxFields;
	
	/**
	 * Sets up the content of the settings panel.
	 */
	public SettingsPanel(){
		// Create the title
		JLabel titleLabel = new JLabel("Settings");
		titleLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 100));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Create a back button
		JButton backButton = new JButton("Back");
		backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		backButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				// Extract and save the settings
				extractSettings();
				// Go back to the main menu
				Utils.mainFrame.toggleSettingsMenu();
			}
		});
		
		// Create a box to store the components
		Box settingsBox = Box.createVerticalBox();
		
		// Add the title
		settingsBox.add(Box.createVerticalStrut(50));
		settingsBox.add(titleLabel);
		// Add the settings fields
		settingsBox.add(Box.createVerticalStrut(30));
		settingsBox.add(createFieldsPanel());
		// Add the back button
		settingsBox.add(Box.createVerticalStrut(40));
		settingsBox.add(backButton);
		
		// Add the box to the panel
		add(settingsBox);
	}
	
	/**
	 * Creates a JPanel containing all the settings fields located in the Settings class.
	 * @return The created JPanel
	 */
	private JPanel createFieldsPanel(){
		// We'll be putting all the settings in a grid layout so that the field names will be
		// displayed alongside the fields.
		// So create a JPanel to store them all in
		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		fieldsPanel.setLayout(new GridLayout(0, 2));
		
		// Create the stacks to store each type of field
		textFields = new Stack<>();
		selectFields = new Stack<>();
		checkboxFields = new Stack<>();
		
		// Go through each setting and add them
		SettingInfo currentSettingInfo;
		for(Entry<Setting, SettingInfo> entry : Settings.SETTINGS_INFO.entrySet()){
			currentSettingInfo = entry.getValue();

			// Create a label to go alongside the field
			JLabel label = new JLabel(currentSettingInfo.fieldName);
			fieldsPanel.add(label);
			
			// Create a field depending on the setting info.
			// Also add each field to its matching field stack and also give it the name of the
			// setting so we can identify it later.
			switch(currentSettingInfo.type){
			case TEXT:{
				// Create the text field
				JTextField textField = new JTextField();
				textField.setName(entry.getKey().name());
				textField.setText(currentSettingInfo.value);
				textFields.push(textField);
				fieldsPanel.add(textField);
				break;
			}
			case SELECT_BOX:{
				// Create the select box
				JComboBox<String> selectBox = new JComboBox<>(currentSettingInfo.selectValues);
				selectBox.setName(entry.getKey().name());
				if(currentSettingInfo.value != null) selectBox.setSelectedItem(currentSettingInfo.value);
				selectFields.push(selectBox);
				fieldsPanel.add(selectBox);
				break;
			}
			case CHECKBOX:{
				// Create the checkbox
				JCheckBox checkbox = new JCheckBox();
				checkbox.setName(entry.getKey().name());
				checkbox.setSelected(Boolean.getBoolean(currentSettingInfo.value));
				checkboxFields.push(checkbox);
				fieldsPanel.add(checkbox);
				break;
			}
			default:{
				Utils.log("Unknown field type: " + currentSettingInfo.type);
				fieldsPanel.add(new JLabel("Unknown"));
				break;
			}
			}
		}
		
		// Return the created fields panel
		return fieldsPanel;
	}
	
	/**
	 * Extracts the current settings from their fields, putting them into their matching
	 * SettingsInfo object and saves them.
	 */
	private void extractSettings(){
		SettingInfo settingInfo;
		
		// Go through each type of field, getting the value from each.
		// Go through the text fields
		for(JTextField textField : textFields){
			settingInfo = Settings.SETTINGS_INFO.get(Setting.valueOf(textField.getName()));
			settingInfo.value = textField.getText();
		}
		// Go through the select box fields
		for(JComboBox<String> selectBox : selectFields){
			settingInfo = Settings.SETTINGS_INFO.get(Setting.valueOf(selectBox.getName()));
			if(selectBox.getSelectedItem() != null){
				settingInfo.value = selectBox.getSelectedItem().toString();
			}
		}
		// Go through the select box fields
		for(JCheckBox checkbox : checkboxFields){
			settingInfo = Settings.SETTINGS_INFO.get(Setting.valueOf(checkbox.getName()));
			settingInfo.value = Boolean.toString(checkbox.isSelected());
		}
		
		// Save the settings
		Settings.saveSettings();
	}
	
}
