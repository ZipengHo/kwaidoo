package cell.example.file;

import cell.ResourceCellIntf;
import cell.ServiceCellIntf;

public interface IFileResource extends ResourceCellIntf {

    String readText() throws Exception;
}

public interface IFileResourceManager extends ServiceCellIntf {

    IFileResource open(String path) throws Exception;
}

package cell.example.file.impl;

import bap.cells.BasicCell;
import bap.cells.BasicServiceCell;
import cell.example.file.IFileResource;
import cell.example.file.IFileResourceManager;

import java.io.BufferedReader;
import java.io.FileReader;

public class CFileResource extends BasicCell implements IFileResource {

    private final String path;

    public CFileResource(String path) {
        this.path = path;
    }

    @Override
    public String readText() throws Exception {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    @Override
    public void onClose() {
    }
}

public class CFileResourceManager extends BasicServiceCell implements IFileResourceManager {

    @Override
    protected void doStartService() throws Exception {
    }

    @Override
    protected void doStopService() {
    }

    @Override
    public IFileResource open(String path) throws Exception {
        return new CFileResource(path);
    }
}

package test;

import bap.cells.Cells;
import bap.tester.BapTester;
import cell.example.file.IFileResource;
import cell.example.file.IFileResourceManager;
import org.junit.Test;

public class FileResourceTest extends BapTester {

    @Test
    public void testLocalResource() throws Exception {
        try (IFileResource resource = new cell.example.file.impl.CFileResource("D:/demo.txt")) {
            System.out.println(resource.readText());
        }
    }

    @Test
    public void testResourceFromManager() throws Exception {
        IFileResourceManager manager = Cells.get(IFileResourceManager.class);
        try (IFileResource resource = manager.open("D:/demo.txt")) {
            System.out.println(resource.readText());
        }
    }
}
