package com.netflix.exhibitor.core.analyze;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.exhibitor.core.Exhibitor;
import org.mockito.Mockito;
import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestPathAnalyzer
{
    @Test
    public void         testBasic() throws Exception
    {
        final Map<PathAndMax, List<String>>       data = Maps.newHashMap();
        data.put(new PathAndMax("/r1", 1), Arrays.asList("p1-0001", "p2-0002"));
        data.put(new PathAndMax("/r2", 1), Arrays.asList("p4-0001", "p1-0002"));
        data.put(new PathAndMax("/r3", 1), Arrays.asList("p2-0001", "p5-0002"));
        data.put(new PathAndMax("/r4", 1), Arrays.asList("p2-0001", "p3-0002"));
        data.put(new PathAndMax("/r5", 1), Arrays.asList("p3-0001", "p4-0002"));
/*
        data.put(new PathAndMax("/r1", 1), Arrays.asList("p1-0001", "p2-0002", "p3-0003"));
        data.put(new PathAndMax("/r2", 1), Arrays.asList("p2-0001", "p1-0002", "p3-0003"));
*/


        Exhibitor       mockExhibitor = Mockito.mock(Exhibitor.class);
        PathAnalyzer    pathAnalyzer = new PathAnalyzer(mockExhibitor, Lists.newArrayList(data.keySet()))
        {
            @Override
            protected List<String> getChildren(String path) throws Exception
            {
                List<String> l = data.get(new PathAndMax(path, 0));
                return (l != null) ? l : Lists.<String>newArrayList();
            }

            @Override
            protected String getId(String fullPath) throws Exception
            {
                return fullPath.substring(4, 6);
            }
        };

        Analysis analysis = pathAnalyzer.analyze();

        System.out.println("Details:");
        System.out.println(analysis.getCompleteData());
        System.out.println();
        System.out.println("Cycles:");
        System.out.println(analysis.getPossibleCycles());
    }

    private static final String         indentStr = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";

    private void outputNode(Node n, int flagValue, int indent)
    {
        System.out.print(indentStr.substring(0, indent));
        if ( n.getFlagValue() == flagValue )
        {
            System.out.println("CYCLE:" + toStr(n));
        }
        else
        {
            n.setFlagValue(flagValue);
            System.out.println(toStr(n));

            for ( Node child : n.getEdges() )
            {
                outputNode(child, flagValue, indent + 1);
            }
        }
    }

    private String toStr(Node n)
    {
        return n.getValue() + " ";
    }
}
