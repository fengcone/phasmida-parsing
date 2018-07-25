# phasmida-parsing
chinese-address-parsing
String address = "陕西省榆林市大柳塔";
ParsingContext context = new ParsingContext();
context.setParsingString(address);
context.parsing();
AnalyzerResponse analyzerResponse = AnalyzerResponseUtil.generateResponse(context);
System.out.println(analyzerResponse);
AnalyzerResponse(provinceName=陕西省, cityName=榆林市, countyName=神木市, townName=大柳塔镇, provinceId=610000, cityId=610800, countyId=610881, townId=610881104, address=陕西省榆林市大柳塔)
邮箱：fengcone@163.com
