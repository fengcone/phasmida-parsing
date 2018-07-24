package com.jd.presort.parsing;

import com.fengcone.phasmida.core.PhasmidaFactory;
import com.fengcone.phasmida.registry.RegistryUtil;
import com.jd.presort.parsing.response.AnalyzerResponse;
import com.jd.presort.parsing.response.AnalyzerResponseUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParsingAddressTest {
    public static void main(String[] args) throws IOException {
        String address = "陕西省榆林市大柳塔";
        ParsingContext context = new ParsingContext();
        context.setParsingString(address);
        String dataPath = "D:\\WorkSpace\\phasmida-parsing\\src\\test\\java\\com\\jd\\presort\\parsing\\rules.data";
        ParsingTree tree = init(new File(dataPath));
        context.setParsingTree(tree);
        context.parsing();
        AnalyzerResponse analyzerResponse = AnalyzerResponseUtil.generateResponse(context);
        System.out.println(analyzerResponse);
        address = "内蒙古康巴什区青春山";
        context.reset();
        context.setParsingString(address);
        context.parsing();
        analyzerResponse = AnalyzerResponseUtil.generateResponse(context);
        System.out.println(analyzerResponse);
    }


    private static ParsingTree init(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<ParsingNode> parsingNodes = new ArrayList<ParsingNode>();
        PhasmidaFactory factory = new PhasmidaFactory();
        RegistryUtil.registerStandardFragments();
        String line;
        while ((line = reader.readLine()) != null) {
            ParsingNode node = new ParsingNode();
            String[] split = line.split(";");
            node.setId(Long.valueOf(split[0]));
            node.setParsingLevel(Integer.valueOf(split[1]));
            String regex = split[2];
            if (!regex.contains("end")) {
                node.setPhasmida(factory.getPhasmida(regex));
            } else {
                String[] split1 = regex.split(".end\\(");
                node.setPhasmida(factory.getPhasmida(split1[0]));
                for (int i = 1; i < split1.length; i++) {
                    String sign = split1[i];
                    if (sign.equals("dc)")) {
                        node.setDengCross(true);
                    } else if (sign.equals("rf)")) {
                        node.setReFatherIndex(true);
                    } else if (sign.equals("r)")) {
                        node.setReIndex(true);
                    }
                }
            }
            node.setTargetId(split[3]);
            node.setFatherId(Long.valueOf(split[4]));
            node.setDescription(split[5]);
            parsingNodes.add(node);
        }
        ParsingTree tree = new ParsingTree();
        tree.init(parsingNodes);
        return tree;
    }
}
