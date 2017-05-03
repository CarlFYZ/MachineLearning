package demo.logisticregression;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * A util class to manage the environment variables/system properties
 * via hadoop .
 */
public class HadoopFileSystemUtil
{

    static Configuration conf = null;

    public static Configuration getConf()
    {
        if (conf == null)
        {
            System.setProperty("HADOOP_USER_NAME", "fzhang");
            conf = new Configuration();
            conf.addResource(new Path("resources/core-site.xml"));
            conf.addResource(new Path("resources/hdfs-site.xml"));
            conf.addResource(new Path("resources/mapred-site.xml"));

            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        }
        return conf;
    }

    public static DataInputStream getInputStream(String filePath) throws IOException
    {
        FileSystem fileSystem = FileSystem.get(getConf());
        Path path = new Path(filePath);
        FSDataInputStream in = fileSystem.open(path);
        return in;

    }

    public static void copyFile(String source, String dest) throws IOException
    {
        FileSystem fileSystem = FileSystem.get(getConf());
        Path srcPath = new Path(source);

        Path dstPath = new Path(dest);
        // Check if the file already exists
        if (!(fileSystem.exists(dstPath)))
        {
            System.out.println("No such destination " + dstPath);
            return;
        }

        try
        {
            fileSystem.copyFromLocalFile(srcPath, dstPath);
            System.out.println("File " + source + "copied to " + dest);
        }
        catch (Exception e)
        {
            System.err.println("Exception caught! :" + e);
        }
        finally
        {
            fileSystem.close();
        }

    }

    public static Path writeFile(InputStream in, String dest) throws IOException
    {
        FileSystem fileSystem = FileSystem.get(getConf());
        Path path = new Path(dest);
        if (fileSystem.exists(path))
        {
            System.out.println("File " + dest + " already exists");
            // will override
            // return path;
        }

        // Create a new file and write data to it.
        FSDataOutputStream out = fileSystem.create(path, true);
        
        byte[] b = new byte[1024];
        int numBytes = 0;
        while ((numBytes = in.read(b)) > 0)
        {
            out.write(b, 0, numBytes);
        }

        // Close all the file descripters
        in.close();
        out.close();
        fileSystem.close();
        return path;
    }

    public static ArrayList<String> list(String url)
    {
        ArrayList<String> fileNames = new ArrayList<String>();
        try
        {
            FileSystem fileSystem = FileSystem.get(getConf());
            FileStatus[] status = fileSystem.listStatus(new Path(url));

            for (int i = 0; i < status.length; i++)
            {
                fileNames.add(status[i].getPath().toString());
                // BufferedReader br = new BufferedReader(new InputStreamReader(fileSystem.open(status[i].getPath())));
                // String line;
                // line = br.readLine();
                // while (line != null)
                // {
                // System.out.println(line);
                // fileNames.add(line);
                // line = br.readLine();
                // }
            }
        }
        catch (Exception e)
        {
            System.out.println("File not found");
        }
        return fileNames;
    }
}
