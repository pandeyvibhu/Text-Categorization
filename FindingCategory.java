import java.io.*;
import java.util.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import org.jsoup.*;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.Morphology;
import java.net.URL;
import java.net.URLConnection;

//Main class containing 4 functions namely main,f,getallwordcount,
public class FindingCategory{
    BaseWord BaseWordobj = new BaseWord();
    List<String> allword = new ArrayList<String>();
    HashSet<String> syn=new HashSet<String>();
    Hashtable<String, Integer> impwords = new Hashtable<String, Integer>();
    String keywords[],temp,line = "";
    static List<String> Filename = new ArrayList<String>();
    int c;
    public static File folder = new File("C:\\Users\\VIBHU\\Documents\\NetBeansProjects\\vibhujhffg");
    
    public static void main(String[] args) {
       
       FindingCategory obj = new FindingCategory();
       NounSynset nounSynset;
       NounSynset[] hyponyms;
       int t=0,i=0,count;
       String temp,line = "";
    // Location of file to read
      String filename = "test.txt";
      System.out.println("Reading files under the folder "+ folder.getAbsolutePath());
    
   
    obj.listFilesForFolder(folder);
    System.out.print("=============Gazab==============\n");
    for(i=0;i<obj.Filename.size();i++)
    System.out.print(obj.Filename.get(i)+"    ");
    System.out.println();
    
    FindingCategory obj3 = new  FindingCategory();
    obj3.listFilesForFolder(folder);
    String fileName = "key.txt";
WikiHtml1 obj1 = new WikiHtml1();

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

        try {

            //For finding size of keywords array
           Scanner sc1 = new Scanner(new File(filename));
            while(sc1.hasNext())
                    {
                         t++;
                         line=sc1.next();
                    }
sc1.close();
obj.keywords = new String[t];
Scanner sc = new Scanner(new File(filename));
t=0;
while(sc.hasNextLine())
{
line=sc.next();
temp = BaseWord.baseform(line);
temp =temp.replaceAll("[^A-Za-z0-9]","");
if(temp.equals("vibhu"))
    continue;
  
  obj.keywords[t++]=temp;
  obj.allword.add(temp);
  System.out.println(temp);

}
 sc.close();
        
 } catch (FileNotFoundException e) 
 {
    e.printStackTrace();
 }
        
        //For adding synonyms to the arraylist
        WordNetDatabase database = WordNetDatabase.getFileInstance();
        for(i=0;i<t;i++)
        {
        Synset[] synsets = database.getSynsets(obj.keywords[i],SynsetType.NOUN);

for (int k=0;k<synsets.length;k++)
    {
     nounSynset = (NounSynset)(synsets[k]);
     hyponyms   =  nounSynset.getHyponyms();
     obj.syn.add(nounSynset.getWordForms()[0]);
     System.out.println(nounSynset.getWordForms()[0]);
    }
        }
    obj.allword.addAll(new ArrayList<String>(obj.syn));
    System.out.println("Printing Arraylist");
    for( i = 0; i < obj.allword.size(); i++) 
    {   
    System.out.print(obj.allword.get(i)+"   ");
    }
  
 System.out.println("\n\n             --------- Printing keyword set by hash set------------      \n");

Hashtable<String, Integer> results1 = new Hashtable<String, Integer>();
        for (String word :obj.allword)
                {
                  Integer v = results1.get(word);

                  if (v == null)
                  {
                    results1.put(word, 1);

                  }
                  else
                  {
                    results1.put(word, v+1);
                  }

                }
       Set set1 = results1.entrySet();
       Iterator itr1 = set1.iterator();
        while(itr1.hasNext())
        {   Map.Entry entry1 = (Map.Entry)itr1.next();
            System.out.println(entry1.getKey()+"      "+entry1.getValue());
        }
//For obtaining wikipedia pages and categories
     

        
        System.out.println("\n             ---     Now words added to Hashset from wiki categories will be printed    ---");
    String q[] = new String[t];
    int num[] =new int[t];
    //Hashtables and Hashmap set will contain all words (tagged words)
       FindingCategory obj2 = new FindingCategory();
       Hashtable<String, Integer> results = new Hashtable<String, Integer>();
       results = obj2.getAllWordCount();
       Set set = results.entrySet();
       
       Iterator itr = set.iterator();
        while(itr.hasNext())
        {   Map.Entry entry = (Map.Entry)itr.next();
            System.out.println(entry.getKey()+"      "+entry.getValue());
        }
        
        System.out.println(" ------------------------  -    -----COMMON WORDS--------    -  -------------------------  ");
       Iterator itr2 = set.iterator();
       
       count=0;
       while(itr2.hasNext())
        {   Map.Entry entry = (Map.Entry)itr2.next();
            int c=0;
            Iterator itr4 = set1.iterator();
             while(itr4.hasNext())
                {       Map.Entry entry4 = (Map.Entry)itr4.next();
                             if(entry.getKey().toString().equals(entry4.getKey().toString()))

                        {
                             num[count] = (Integer)entry4.getValue();
                             q[count++] = (String)entry.getKey();
                             c=1;
                        }
                    if(c==0)
                       continue;
                    else
                    {
                       c=0;
                       System.out.print((String)entry.getKey()+"   ");
                       System.out.println((Integer)entry.getValue()* (Integer)entry4.getValue());
                    }
                }
        }
       
     int s=count;
     Hashtable<String, Integer> Categories = new Hashtable<String, Integer>();
try{
String word2="";
FileReader fileReaderobj = new FileReader("categoryfile.txt");
BufferedReader bufferedReaderobj = new BufferedReader(fileReaderobj);

while((line = bufferedReaderobj.readLine()) != null)
{
   count=0;
    for( i=0;i<s;i++)
    {   if(line.toLowerCase().contains(q[i].toLowerCase()))
          {word2 =line;count=1;}
System.out.print(num[i]+"   ");
        if(count==1)
        {  count =0;
            Integer c = Categories.get(word2);

                  if (c == null)
                  {
                   Categories.put(word2, 1*num[i]);
                   //Categories.put(word2, 1);
                  }
                  else
                  {
                  Categories.put(word2, (c+1)*num[i]);
                 // Categories.put(word2, (c+1));
                  }

        }
    }
}
System.out.println();
Set sett = Categories.entrySet();
Iterator itr3 = sett.iterator();
while(itr3.hasNext())
           {   Map.Entry entry3= (Map.Entry)itr3.next();
               System.out.println(entry3.getKey()+"      "+entry3.getValue());
           }
  Categories.clear();
bufferedReaderobj.close();
}
catch(FileNotFoundException ex)
{
System.out.println("Unable to open file '" +"categoryfile.txt" + "'");
}
catch(IOException ex)
{
ex.printStackTrace();
}

}
    //FOR NAMING FILES AND PUTTING THAT IN Filename array
      public void listFilesForFolder(final File folder) {
    for (final File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
            listFilesForFolder(fileEntry);
        }  else {
           String s= fileEntry.getName();
           if(s.startsWith("Filecat"))
           {
               System.out.println(s);
               Filename.add(s);
           }
        }
    }
}
  //Get All Word Count for wikipedia categories
   private  Hashtable<String, Integer> getAllWordCount()
      {
        Hashtable<String, Integer> result = new Hashtable<String, Integer>();
        HashSet<String> words = new HashSet<String>();
        HashSet<String> impwordset = new HashSet<String>();

        try {
        System.out.println("Printing Arraylist");
        
        for( int i = 0; i <Filename.size(); i++) {   
        System.out.print(Filename.get(i)+"   ");
        }
           BufferedReader[] sc =new BufferedReader[Filename.size()];

             for(int i=0;i<Filename.size();i++){
                sc[i] = new BufferedReader(new FileReader(Filename.get(i)));

             while((line = sc[i].readLine()) != null)
                {
                   System.out.println(line);
                   String text =line.replaceAll("[^A-Za-z0-9]","");
                 //  String text =sc[i].nextLine();
                   StringTokenizer st = new StringTokenizer(line," ",false);
                 //
                   if(line.contains("articles")||line.contains("Articles"));
                   else
                   f(line);
                   while (st.hasMoreTokens())
                   {
                   StringTokenizer st1 = new StringTokenizer(st.nextToken(),"-",false);

                   while (st1.hasMoreTokens())
                   {
                   String s= "";
                   s=st1.nextToken();
                    String p = BaseWordobj.baseform(s);
                     if(p.equals("vibhu"));
                     else
                    { if(p.contains("20") || p.contains("dmy") || p.contains("wikidata"))
                           continue;
                        words.add(p);
                        
                        impwordset.add(text);
                        System.out.println(p);
                     }
                   }
                   //words.remove(i);
                   }
                   //words.add(text);

                }

                //For globalHashtable impwords it contains line as key element
for (String word1 : impwordset)
                {
                  Integer count = impwords.get(word1);

                  if (count == null)
                  {
                    impwords.put(word1, 1);
                  }
                  else
                  {
                    impwords.put(word1, count + 1);
                  }

                }
                impwordset.clear();

         //for hashtable result which is returned as results to main it contains tagged words.
           for (String word : words)
                {
                  Integer count = result.get(word);

                  if (count == null)
                  {
                    result.put(word, 1);

                  }
                  else
                  {
                    result.put(word, count + 1);
                  }

                }
                words.clear();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(result.size());
        return result;

}
   
   String catfile= "categoryfile.txt";
   int d;
 void f(String s)
{
try {
    boolean b;
if(d==0)
    b=false;
else
    b=true;

FileWriter fileWriter = new FileWriter(catfile,b);
BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    bufferedWriter.newLine();
    bufferedWriter.write(s);
    bufferedWriter.close();
    d=1;
   }
catch(IOException ex) {
System.out.println("Error writing to file '"+catfile+ "'");

  }

}

}
    


class BaseWord {

    static String baseform(String s)
    {
        String a="";
		System.setProperty("wordnet.database.dir", "C:\\Users\\VIBHU\\Documents\\NetBeansProjects\\PreProject\\dict\\");
		WordNetDatabase database = WordNetDatabase.getFileInstance();

		Morphology id = Morphology.getInstance();

         String[] arr2 = id.getBaseFormCandidates(s,SynsetType.VERB);
         //System.out.println(arr2.length);
        if(arr2.length!=0)
        {
            if(arr2.length==1)
                a=arr2[0].toLowerCase();
            else
                a=arr2[1].toLowerCase();
        }
        else a=s.toLowerCase();

        String[] arr1 =
                           {"aboard","about","above","across","after","against","along","amid","among","anti","around","as","at","is","am","are","ha","i","thi","have","also",
                            "the","before","behind","below","be","beneath","need","besid","between","beyond","beside","but","by","a","an","the","concern","too","me","him","and",
                            "besid","from","in","inside","into","concern","consider","despite","down","during","except","contain","exclud","all","language","wikipedia","article",
                            "like","minus","near","of","off","statement","on","onto","opposite","outside","over","past","per","plus","regard","be","gnd","hi","it","these","under",
                            "round","save","that","since","than","through","to","toward","towards","under","underneath","unlike","until","up","upon","versus","via","with","or",
                            "wa","his","her","she","identifier","link","he","use","external","title","and","viaf","within","protect","page","link","without","following","for","dmy","category","indefinitely"};

              for(int i=0;i<arr1.length;i++)
                    if(a.equalsIgnoreCase(arr1[i]))
                      a="vibhu";


        return(a);

   }


}
class google_search{
 void f() throws IOException{
String google = "http://www.google.com/search?q=";
String search = "kareena kapoor";
String charset = "UTF-8";
String userAgent = "Wikipedia"; // Change this to your company's name and bot homepage!
//Jsoup.clean(google, search, null);
//Elements links =  Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select("li.g>h3>a");
 org.jsoup.select.Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select("li.g>h3>a");

for (org.jsoup.nodes.Element link : links) {
    String title = link.text();
    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

    if (!url.startsWith("http")) {
        continue; // Ads/news/etc.
    }

    System.out.println("Title: " + title);
    System.out.println("URL: " + url);
}

}}


class WikiHtml1 {
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

class CategoryExtraction1
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