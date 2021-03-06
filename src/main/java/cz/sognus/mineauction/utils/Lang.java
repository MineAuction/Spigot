package cz.sognus.mineauction.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.yaml.snakeyaml.Yaml;

import cz.sognus.mineauction.MineAuction;

/**
 * This class manage language files.
 *
 * @author Sognus
 * 
 * 
 *         TODO: Opravit nemožnost editovat soubor po reloadu pluginu (file lock)
 */
public class Lang {

	private HashMap<String, Object> messages = new HashMap<String, Object>();;

	private MineAuction plugin;
	private String lang;

	public Lang(MineAuction plugin) throws Exception {
		this.plugin = plugin;
		this.copyLanguageFiles();
		this.lang = MineAuction.config.getString("plugin.general.lang");
		this.loadLang();
	}

	@SuppressWarnings("unchecked")
	public void loadLang() throws Exception {
		Yaml yaml = new Yaml();
		File file = new File(plugin.getDataFolder() + "/lang/" + lang + ".yml");
		InputStream is = new FileInputStream(file);
		Map<String, Object> result = (Map<String, Object>) yaml.load(is);
		messages.putAll(result);
		is.close();
		file.setWritable(true);
	}

	public void copyLanguageFiles() {
		File langFolder = new File(plugin.getDataFolder() + "/lang/");
		if (!langFolder.exists())
			langFolder.mkdirs();

		try {
			JarFile jarFile = new JarFile(plugin.getClass()
					.getProtectionDomain().getCodeSource().getLocation()
					.toURI().getPath());

			for (Enumeration<JarEntry> em = jarFile.entries(); em
					.hasMoreElements();) {
				String s = em.nextElement().toString();

				if (s.startsWith("lang/")) {
					ZipEntry entry = jarFile.getEntry(s);

					String fileName = s.substring(s.lastIndexOf("/") + 1,
							s.length());

					if (fileName.endsWith(".yml")) {

						File eFile = new File(plugin.getDataFolder() + "/lang/"
								+ fileName);
						if (eFile.exists())
							continue;

						InputStream inStream = jarFile.getInputStream(entry);
						OutputStream out = new FileOutputStream(
								plugin.getDataFolder() + "/lang/" + fileName);

						int c;

						while ((c = inStream.read()) != -1) {
							out.write(c);
						}
						eFile.setWritable(true);
						inStream.close();
						out.close();

					}
				}
			}
			jarFile.close();

		} catch (Exception e) {
			e.printStackTrace();
			Log.warning("Unable to copy language files");
		}

	}

	@SuppressWarnings("unchecked")
	public String getString(String key) {
		try {

			Yaml yaml = new Yaml();
			File file = new File(plugin.getDataFolder() + "/lang/" + lang
					+ ".yml");
			InputStream is = new FileInputStream(file);
			Map<String, Object> data = (Map<String, Object>) yaml.load(is);

			if (messages.get(key) != null)
				return (String) messages.get(key);

			FileWriter writer = new FileWriter(file);
			data.put(key, "<<Required content not found>>");
			yaml.dump(data, writer);

			writer.close();
			is.close();
			file.setWritable(true);

			return "<<Required content not found>>";
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.warning("An error occurred while trying to get language content");
		return "<<An error occurred while trying to get language content>>";

	}

	public static void deleteLangFiles() {
		File dir = new File(MineAuction.plugin.getDataFolder() + "/lang/");
		for (File file : dir.listFiles()) {
			file.delete();
		}

	}

}
