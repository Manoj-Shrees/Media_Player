package com.dreamhunterztech.media_player.datahandlers;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Dreamer on 06-11-2017.
 */

public class offlinedatahandler {
    String fileurl,acessfilename,filename;
    Element root;

    public offlinedatahandler(String url, String acessfilename, String filename)
    {
        this.fileurl = url;
        this.acessfilename =  acessfilename;
        this.filename = filename;
    }


    public void createfile() throws IOException {
        File roots = new File(Environment.getExternalStorageDirectory(),fileurl);
        File file =new File(roots ,acessfilename);
        //Create the file
        if (file.createNewFile()) {
            Log.e("chk","File is created!");
        } else {
            Log.e("chk","File is already exist");
        }

        //Write Content
        FileWriter writer = new FileWriter(file);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<resources>\n" +
                "</resources>");
        writer.close();
    }



    public void createnode() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        File roots = new File(Environment.getExternalStorageDirectory(),fileurl);
        File filepath = new File(roots ,acessfilename);
        Document document = documentBuilder.parse(filepath);

        root = document.getDocumentElement();

        Collection<Data> filedata = new ArrayList<Data>();
        filedata.add(new Data(filename , roots+"/"+filename));

        for (Data data : filedata) {

            Element newgfile = document.createElement("File");

            Element name = document.createElement("filename");
            name.appendChild(document.createTextNode(data.getFilename()));
            newgfile.appendChild(name);

            Element port = document.createElement("fileurl");
            port.appendChild(document.createTextNode(data.getFileurl()));
            newgfile.appendChild(port);

            root.appendChild(newgfile);

        }

        commitfiletosave(document,filepath);

    }



    public void deletefile(int position) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        File roots = new File(Environment.getExternalStorageDirectory(),fileurl);
        File filepath = new File(roots,filename);
        File datafilepath = new File(roots ,acessfilename);
        Document document = documentBuilder.parse(datafilepath);
        root = document.getDocumentElement();
        Node categories = root.getElementsByTagName("File").item(position);
        root.removeChild(categories);

        if(filepath.delete())
        commitfiletosave(document,datafilepath);

    }


    private void commitfiletosave(Document document, File filepath) throws TransformerException {
        DOMSource source = new DOMSource(document);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(filepath);
        transformer.transform(source, result);

    }



    public static class Data {
        String filename , fileurl;

        Data(String filename , String fileurl)
        {
            this.filename = filename;
            this.fileurl = fileurl;
        }

        public String getFilename() {
            return filename;
        }

        public String getFileurl() {
            return fileurl;
        }

    }
}
