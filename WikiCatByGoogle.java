import java.io.*;
import java.net.*;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import static jdk.nashorn.internal.objects.NativeString.search;
import org.jsoup.*;


public class WikiCatByGoogle{
     public static File folder = new File("C:\\Users\\VIBHU\\Documents\\NetBeansProjects\\vibhujhffg");
     static String temp = "";

     public static void main(String a[]) throws IOException{
         System.out.println("Reading files under the folder "+ folder.getAbsolutePath());
   
    String fileName = "key.txt";
WikiHtml obj1 = new WikiHtml();
String line = "";
try {
FileReader fileReaderobj = new FileReader(fileName);
BufferedReader bufferedReaderobj = new BufferedReader(fileReaderobj);
String google = "http://www.google.com/search?q=";
String charset = "UTF-8";
String search="iit",userAgent = "Wikipedia";
while((line = bufferedReaderobj.readLine()) != null) {
System.out.println(line);
search = line+"wikipedia";
org.jsoup.select.Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select("li.g>h3>a");
int m=0;
for (org.jsoup.nodes.Element link : links) {
    if(m==0){
    String title = link.text();
    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

    if (!(url.startsWith("http")&& url.contains("wikipedia"))) {
        continue; // Ads/news/etc.
    }
    if(url.charAt(4)=='s');
    else
    url = "https"+url.substring(4);
    
    System.out.println("Title: " + title);
    System.out.println("URL: " + url);
      obj1.Wiki(url);
      m++;
    }
    else 
        break;
}
}
 
bufferedReaderobj.close();
}
catch(FileNotFoundException ex){
System.out.println("Unable to open file '" +fileName + "'");

}
catch(IOException ex){
ex.printStackTrace();

}
 
   WikiCatByGoogle obj = new WikiCatByGoogle();
    obj.listFilesForFolder(folder);
}
     
       public void listFilesForFolder(final File folder) {
    for (final File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
            listFilesForFolder(fileEntry);
        }  else {
           String s= fileEntry.getName();
           if(s.startsWith("Filecat"))
           {
               System.out.println(s);
           }
        }
    }
}
}


class WikiHtml {
int k=1;
void Wiki(String s) {
CategoryExtraction obj1 = new CategoryExtraction();
String fileName2 = "file"+ k +".txt";
try {
System.out.println("Pointer Reached Wiki function");
File file = new File(fileName2);
FileWriter fileWriter2 = new FileWriter(fileName2);
BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
//Writing from net to a file
URL oracle = new URL(s);
URLConnection yc = oracle.openConnection();
BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
String inputLine;
while ((inputLine = in.readLine()) != null)
{
    bufferedWriter2.write(inputLine);
    bufferedWriter2.newLine();
}
k++;
obj1.extract(fileName2);
in.close();
bufferedWriter2.close();
}
catch(IOException ex) {
System.out.println("Error writing to file '"+ fileName2+ "'");

}
}
}

class CategoryExtraction
{  
  int k;
  String fileName;
void extract(String s) {
CategoryExtraction o = new CategoryExtraction();
 fileName = s;

String line = "",a="",b="";

try {
FileReader fileReaderobj = new FileReader(fileName);
BufferedReader bufferedReaderobj = new BufferedReader(fileReaderobj);
int i=0,j,l;
int num=1;
while((line = bufferedReaderobj.readLine()) != null)
{
        
        if(line.contains("<div id='catlinks' class='catlinks'><div id="))
        {
            line = line.trim();
            l=line.length();
        System.out.println(line);

        System.out.println("\nLength of line = "+l+"\n");
        int n = line.indexOf('<');
       
                for(i=n;i<l-4;i++)
                { if(line.charAt(i)=='<'&& line.charAt(i+1)=='/'&&line.charAt(i+2)=='a')
                  {for(j=i;true;j--)
                            if(line.charAt(j)=='>')
                            {  b=line.substring(j+1, i);
                                System.out.print(num+++". ");
                                System.out.println(b);
                                o.f1(b);
                                break;
                            }
                  }
               }
        }
}
     k++;
bufferedReaderobj.close();
}
catch(FileNotFoundException ex){
System.out.println("Unable to open file '" +fileName + "'");
}
catch(IOException ex){
 ex.printStackTrace();
}
}

void f1(String s){
  try {
FileWriter fileWriter = new FileWriter("Filecat"+k+++".txt",true);
BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
bufferedWriter.newLine();
bufferedWriter.write(s);
bufferedWriter.close();
}
catch(IOException ex) {
System.out.println("Error writing to file '"+ fileName + "'");
}
}
}