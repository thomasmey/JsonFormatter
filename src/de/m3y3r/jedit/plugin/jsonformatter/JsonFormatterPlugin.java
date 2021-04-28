package de.m3y3r.jedit.plugin.jsonformatter;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;

public class JsonFormatterPlugin extends EditPlugin {

	private static JsonFormatterPlugin instance;
	private final JsonWriterFactory jwf;

	@Override
	public void start() {
		super.start();
		instance = this;
	}

	@Override
	public void stop() {
		instance = null;
		super.stop();
	}

	public static JsonFormatterPlugin getInstance() {
		return instance;
	}

	public JsonFormatterPlugin() {
		Map jsonConfig = new HashMap();
		jsonConfig.put(JsonGenerator.PRETTY_PRINTING, true);
		this.jwf = Json.createWriterFactory(jsonConfig);
	}

	public void formatJson(JEditTextArea textArea) {
		for(Selection s: textArea.getSelection()) {
			var selectedText = textArea.getSelectedText(s);
			try(StringReader sr = new StringReader(selectedText);
				JsonReader jr = Json.createReader(sr);) {
				JsonValue jv = jr.readValue();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try(JsonWriter jw = jwf.createWriter(baos)) {
					jw.write(jv);
				}
				textArea.setSelectedText(s, baos.toString());
			}
		}
	}
}
