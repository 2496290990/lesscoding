import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

import java.time.LocalDateTime

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */

typeMapping = [
        (~/(?i)int/)                      : "Integer",
        (~/(?i)float|double|decimal|real/): "Double",
        (~/(?i)datetime|timestamp/)       : "LocalDateTime",
        (~/(?i)date/)                     : "LocalDate",
        (~/(?i)time/)                     : "LocalTime",
        (~/(?i)/)                         : "String"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = javaName(table.getName(), true)
    def fields = calcFields(table)
    def classNameResult = getAllClassAndAlias(className, dir.toString())
    //通用返回类型
    def commonResult = "R"
    def resultClass = "com.huazheng.tunny.common.core.util.R";
    /*
    new File(dir, className + ".java").withPrintWriter("utf-8") {
        out -> generate(out, className, fields, table.getName(), table.getComment(), getPackageName(dir.toString()))
    }*/
    //生成entity
    new File(getPath(dir.toString(), "entity"), className + ".java").withPrintWriter("utf-8") {
        out -> generateEntity(out, className, fields, table.getName(), table.getComment(), classNameResult)
    }
    //生成controller
    new File(getPath(dir.toString(), "controller"), classNameResult.controller + ".java").withPrintWriter("utf-8") {
        out -> generateController(out, className, table.getComment(), classNameResult, commonResult, resultClass, table.getName())
    }
    //生成service
    new File(getPath(dir.toString(), "service"), classNameResult.service + ".java").withPrintWriter("utf-8") {
        out -> generateService(out, className, table.getComment(), classNameResult, commonResult, resultClass)
    }
    //生成impl
    new File(getPath(dir.toString(), "service/impl"), classNameResult.impl + ".java").withPrintWriter("utf-8") {
        out -> generateImpl(out, className, table.getComment(), classNameResult, commonResult, resultClass)
    }
    //生成Mapper
    new File(getPath(dir.toString(), "mapper"), classNameResult.mapper + ".java").withPrintWriter("utf-8") {
        out -> generateMapper(out, className, table.getComment(), classNameResult)
    }
    //生成xml
    new File(getPath(dir.toString(), "xml"), classNameResult.mapper + ".xml").withPrintWriter("utf-8") {
        out -> generateXml(out, className, fields, table.getName(), table.getComment(), classNameResult)
    }

}
/**
 * 生成实体类
 * @param out 输出对象
 * @param className 类名
 * @param fields 类字段
 * @param tableName 表名
 * @param tableComment 表注释
 * @param dir 输出路径
 * @return
 */
def generate(out, className, fields, tableName, tableComment, dir) {
    out.println "package $dir;"
    out.println ""
    out.println ""
    out.println "/**"
    out.println " * @author eleven "
    out.println " * @date " + new Date().toLocaleString()
    out.println " * @description $tableComment   $tableName"
    out.println " */"
    out.println " "
    out.println "@Data"
    out.println "@AllArgsConstructor"
    out.println "@NoArgsConstructor"
    out.println "@TableName(\"$tableName\")"
    out.println "public class $className {"
    out.println ""
    fields.each() {
        if (it.annos != "") out.println "  ${it.annos}"
        out.println "  /**"
        out.println "   * ${it.comment}"
        out.println "   */"
        out.println "  private ${it.type} ${it.name};"
    }
    out.println ""
    out.println "}"
}

def generateEntity(out, className, fields, tableName, tableComment,classNameResult) {
    out.println "package $classNameResult.entity;"
    out.println ""
    out.println ""
    out.println "/**"
    out.println " * @author eleven "
    out.println " * @date " + new Date().toLocaleString()
    out.println " * @description $tableComment   $tableName"
    out.println " */"
    out.println " "
    out.println "@Data"
    out.println "@AllArgsConstructor"
    out.println "@NoArgsConstructor"
    out.println "@TableName(\"$tableName\")"
    out.println "public class $className {"
    out.println ""
    fields.each() {
        if (it.annos != "") out.println "  ${it.annos}"
        out.println "  /**"
        out.println "   * ${it.comment}"
        out.println "   */"
        out.println "  private ${it.type} ${it.name};"
    }
    out.println ""
    out.println "}"
}

/**
 * 生成 controller
 * @param out 输出对象
 * @param className 类名
 * @param fields 表字段
 * @param tableName 表名
 * @param tableComment 表注释
 * @param dir 输出路径
 * @return
 */
def generateController(out, className, tableComment,classNameResult, commonResult, resultClass, tableName) {
    out.println "package $classNameResult.controllerPackage;"
    out.println ""
    out.println "import org.springframework.beans.factory.annotation.Autowired;"
    out.println "import org.springframework.web.bind.annotation.*;"
    out.println "import $classNameResult.servicePackage" + "." + classNameResult.service + ";"
    out.println "import $classNameResult.classPackage;"
    out.println "/**"
    out.println "* @author eleven"
    out.println "* @date" + new Date().toLocaleString()
    out.println "* @description $tableComment " + new String("控制器")
    out.println "*/"
    out.println "@RestController"
    out.println "@RequestMapping(\"/" + getMappingValue(tableName) + "\")"
    out.println "public class $classNameResult.controller {"
    out.println "\t@Autowired"
    out.println "\tprivate  $classNameResult.service $classNameResult.serviceAlias ;"
    out.println "\t@GetMapping(\"/page\")"
    out.println "\tpublic $commonResult getPageByLike(@RequestParam $classNameResult.className $classNameResult.classAlias ){"
    out.println "\t\treturn $classNameResult.serviceAlias" + ".getPageByLike($classNameResult.classAlias);"
    out.println "\t}"
    out.println ""
    out.println "\t@GetMapping(\"/{id}\")"
    out.println "\tpublic $commonResult get$classNameResult.className" + "ById(@PathVariable(\"id\") String id){"
    out.println "\t\treturn $classNameResult.serviceAlias" + ".get$classNameResult.className" + "ById(id);"
    out.println "\t}"
    out.println ""
    out.println "\t@PostMapping"
    out.println "\tpublic $commonResult save" + "$classNameResult.className(@RequestBody $classNameResult.className $classNameResult.classAlias ){"
    out.println "\t\treturn $classNameResult.serviceAlias" + ".save$classNameResult.className" + "($classNameResult.classAlias);"
    out.println "\t}"
    out.println ""
    out.println "\t@DeleteMapping(\"/{id}\")"
    out.println "\tpublic $commonResult del$classNameResult.className" + "ById(@PathVariable(\"id\") String id){"
    out.println "\t\treturn $classNameResult.serviceAlias" + ".del$classNameResult.className" + "ById(id);"
    out.println "\t}"
    out.println ""
    out.println "\t@PutMapping"
    out.println "\tpublic $commonResult update$classNameResult.className" + "ById(@RequestBody $classNameResult.className $classNameResult.classAlias ){"
    out.println "\t\treturn $classNameResult.serviceAlias" + ".update$classNameResult.className" + "ById($classNameResult.classAlias);"
    out.println "\t}"
    out.println "}"
}

/**
 *
 * @param out
 * @param className
 * @param tableComment
 * @param dir
 * @param classNameResult
 * @param commonResult
 * @param resultClass
 * @return
 */
def generateService(out, className, tableComment,classNameResult, commonResult, resultClass) {
    out.println "package $classNameResult.servicePackage;"
    out.println ""
    out.println "import $classNameResult.classPackage;"
    out.println "import com.baomidou.mybatisplus.service.IService;"
    out.println "/**"
    out.println " * @author eleven"
    out.println " * @date " + new Date().toLocaleString()
    out.println " * @apiNote $tableComment Service"
    out.println " */"
    out.println "public interface $classNameResult.service extends IService<$classNameResult.className>{"
    out.println ""
    out.println "    $commonResult getPageByLike($classNameResult.className $classNameResult.classAlias);"
    out.println ""
    out.println "    $commonResult get$classNameResult.className" + "ById(String id);"
    out.println ""
    out.println "    $commonResult save$classNameResult.className($classNameResult.className $classNameResult.classAlias);"
    out.println ""
    out.println "    $commonResult del$classNameResult.className" + "ById(String id);"
    out.println ""
    out.println "    $commonResult update$classNameResult.className" + "ById($classNameResult.className $classNameResult.classAlias);"
    out.println ""
    out.println "}"
}
/**
 * 生成 Impl实现类
 * @param out 输出对象
 * @param className 类名
 * @param fields 表字段
 * @param tableName 表名
 * @param tableComment 表注释
 * @return
 */
def generateImpl(out, className, tableComment,classNameResult, commonResult, resultClass) {
    out.println "package $classNameResult.implPackage;"
    out.println ""
    out.println "import com.baomidou.mybatisplus.service.impl.ServiceImpl;"
    out.println "import com.baomidou.mybatisplus.plugins.pagination.PageHelper;"
    out.println "import $classNameResult.classPackage;"
    out.println "import $classNameResult.mapperPackage." + classNameResult.mapper + ";"
    out.println "import $classNameResult.servicePackage." + classNameResult.service + ";"
    out.println "import $resultClass;"
    out.println "import org.springframework.beans.factory.annotation.Autowired;"
    out.println "import org.springframework.stereotype.Service;"
    out.println ""
    out.println "import java.util.List;"
    out.println ""
    out.println "/**"
    out.println " * @author eleven"
    out.println " * @date " + new Date().toLocaleString()
    out.println " * @apiNote $tableComment Service 实现类"
    out.println " */"
    out.println ""
    out.println "@Service"
    out.println "public class $classNameResult.impl extends ServiceImpl<$classNameResult.mapper, $classNameResult.className> implements $classNameResult.service {"
    out.println ""
    out.println "    @Autowired"
    out.println "    private $classNameResult.mapper $classNameResult.mapperAlias;"
    out.println ""
    out.println "    @Override"
    out.println "    public $commonResult getPageByLike($classNameResult.className $classNameResult.classAlias) {"
    out.println "        PageHelper.startPage(1, 10);"
    out.println "        List<$classNameResult.className> list = $classNameResult.mapperAlias" + ".getPageByLike($classNameResult.classAlias);"
    out.println "        return new $commonResult(new PageInfo<$classNameResult.className>(list));"
    out.println "    }"
    out.println ""
    out.println "    @Override"
    out.println "    public $commonResult get$classNameResult.className" + "ById(String id) {"
    out.println "        return new $commonResult($classNameResult.mapperAlias" + ".get$classNameResult.className" + "ById(id));"
    out.println "    }"
    out.println ""
    out.println "    @Override"
    out.println "    public $commonResult save$classNameResult.className($classNameResult.className $classNameResult.classAlias) {"
    out.println "        return new $commonResult($classNameResult.mapperAlias" + ".save$classNameResult.className" + "($classNameResult.classAlias));"
    out.println "    }"
    out.println ""
    out.println "    @Override"
    out.println "    public $commonResult del$classNameResult.className" + "ById(String id) {"
    out.println "        $classNameResult.className $classNameResult.classAlias = new $classNameResult.className();"
    out.println "        $classNameResult.classAlias" + ".setRowId(id);"
    out.println "        $classNameResult.classAlias" + ".setDeleteFlag(\"Y\");"
    out.println "        return new $commonResult($classNameResult.mapperAlias" + ".updateById($classNameResult.classAlias));"
    out.println "    }"
    out.println ""
    out.println "    @Override"
    out.println "    public $commonResult update$classNameResult.className" + "ById($classNameResult.className $classNameResult.classAlias) {"
    out.println "        return new $commonResult($classNameResult.mapperAlias" + ".updateById($classNameResult.classAlias));"
    out.println "    }"
    out.println ""
    out.println ""
    out.println "}"
}

/**
 * 生成mapper
 * @param out
 * @param className
 * @param tableComment
 * @param classNameResult
 * @return
 */
def generateMapper(out, className, tableComment, classNameResult) {
    out.println "package $classNameResult.mapperPackage;"
    out.println ""
    out.println "import com.baomidou.mybatisplus.mapper.BaseMapper;"
    out.println "import $classNameResult.classPackage;"
    out.println "import org.apache.ibatis.annotations.Param;"
    out.println ""
    out.println "import java.util.List;"
    out.println ""
    out.println "/**"
    out.println " * @author eleven"
    out.println " * @date " + new Date().toLocaleString()
    out.println " * @apiNote "
    out.println " */"
    out.println "public interface $classNameResult.mapper extends BaseMapper<$classNameResult.className> {"
    out.println ""
    out.println "    List<$classNameResult.className> getPageByLike($classNameResult.className $classNameResult.classAlias);"
    out.println ""
    out.println "    $classNameResult.className get$classNameResult.className" + "ById(String id);"
    out.println ""
    out.println "    int save$classNameResult.className($classNameResult.className $classNameResult.classAlias);"
    out.println ""
    out.println "    int update$classNameResult.className" + "ById($classNameResult.className $classNameResult.classAlias);"
    out.println ""
    out.println "    int insertBat(@Param(\"list\")List<$classNameResult.className> list);"
    out.println ""
    out.println "    int updateBat(@Param(\"list\")List<$classNameResult.className> list);"
    out.println "}"
}
/**
 * 生成 mapper.xml
 * @param out 输出对象
 * @param className 类名
 * @param fields 表字段
 * @param tableName 表名
 * @param tableComment 表注释
 * @return
 */
def generateXml(out, className, fields, tableName, tableComment, classNameResult) {
    out.println "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
    out.println "<!DOCTYPE mapper"
    out.println "  PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\""
    out.println "  \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">"
    out.println "<mapper namespace=\"$classNameResult.mapperPackage" + ".$classNameResult.mapper\">"
    out.println "    <resultMap id=\"baseMap\" type=\"$classNameResult.classPackage\">"
    String columns = "";
    String[] properties = new String[fields.size()]
    i = 0;
    fields.each() {
        String property = it.name
        properties[i++] = "#{&" + property + "}, "
        String column = humpToUnderLine(property)
        columns += (column + " , ")
        out.println "        <result property=\"$property\" column=\"$column\" />"
    }
    columns = columns.substring(0, columns.length() - 2)
    out.println "    </resultMap>"
    out.println ""
    out.println "    <sql id=\"baseColumn\">"
    out.println "       $columns"
    out.println "    </sql>"
    out.println "    <sql id=\"baseQuery\">"
    out.println "       select "
    out.println "           $columns "
    out.println "       from $tableName tb"
    out.println "    </sql>"

    out.println "    <insert id=\"save$classNameResult.className\">"
    out.println "       insert into $tableName("
    out.println "           $columns"
    out.println "       ) value ("
    out.print "            "
    int i = 0;
    properties.each() {
        String item = it.toString().replaceFirst("&","")
        if(i == properties.length - 1){
            item = item.substring(0,item.length() - 2)
        }
        out.print "$item"
        i++
    }
    out.println ""
    out.println "       )"
    out.println " "
    out.println "    </insert>"
    out.println "    <insert id=\"insertBat\">"
    out.println ""
    out.println "    </insert>"
    out.println "    <update id=\"update$classNameResult.className" + "ById\">"
    out.println ""
    out.println "    </update>"
    out.println "    <update id=\"updateBat\">"
    out.println ""
    out.println "    </update>"
    out.println "    <select id=\"getPageByLike\" resultMap=\"baseMap\">"
    out.println "       <include refid=\"baseQuery\" />"
    out.println "    </select>"
    out.println "    <select id=\"get$classNameResult.className" + "ById\" resultMap=\"baseMap\">"
    out.println "       <include refid=\"baseQuery\" />"
    out.println "       where id = #{id}"
    out.println "    </select>"
    out.println "</mapper>"
}
/**
 * 生成 前端js APi
 * @param out 输出对象
 * @param className 类名
 * @param fields 表字段
 * @param tableName 表名
 * @param tableComment 表注释
 * @return
 */
def generateApi(out, className, fields, tableName, tableComment) {}
/**
 * 生成 vue页面
 * @param out 输出对象
 * @param className 类名
 * @param fields 表字段
 * @param tableName 表名
 * @param tableComment 表注释
 * @return
 */
def generateVue(out, className, fields, tableName, tableComment) {}


/**
 * 获取表里边的字段
 * @param table
 * @return
 */
def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[
                           name   : javaName(col.getName(), false),
                           type   : typeStr,
                           annos  : "",
                           comment: col.getComment()
                   ]]
    }
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

/**
 * 获取包名
 * @param str
 * @return
 */
def getPackageName(str) {
    def path = str.split("java");
    path[1].replaceAll(/[\\]/, ".")
            .substring(1)
}

/**
 * 获取其他的包名
 * @param str
 */
def getOtherPackageName(String str, String packageName) {
    return str.substring(0, str.lastIndexOf(".") + 1) + packageName
}


/**
 * 首字母转小写
 * @param str 类名
 * @return
 */
def initialToLowerCase(String str) {
    return new String(str.charAt(0)).toLowerCase() + str.substring(1)
}
/**
 * 获取所有的类名
 * @param className 类名
 */
def getAllClassAndAlias(String className, String dir) {
    def packageName = getPackageName(dir)
    def result = [
            controller       : className + "Controller",
            controllerAlias  : initialToLowerCase(className) + "Controller",
            controllerPackage: getOtherPackageName(packageName, "controller"),
            service          : className + "Service",
            serviceAlias     : initialToLowerCase(className) + "Service",
            servicePackage   : getOtherPackageName(packageName, "service"),
            impl             : className + "ServiceImpl",
            implAlias        : initialToLowerCase(className) + "ServiceImpl",
            implPackage      : getOtherPackageName(packageName, "service.impl"),
            mapper           : className + "Mapper",
            mapperAlias      : initialToLowerCase(className) + "Mapper",
            mapperPackage    : getOtherPackageName(packageName, "mapper"),
            className        : className,
            classAlias       : initialToLowerCase(className),
            classPackage     : getOtherPackageName(packageName, "entity") + "." + className,
            entity           : getOtherPackageName(packageName, "entity")
    ]
}

/**
 * 获取路径
 * @param dir
 * @param filePath
 * @return
 */
def getPath(String dir, String filePath) {
    dir = dir.substring(0, dir.lastIndexOf(File.separator) + 1)
    new File(dir + filePath).mkdirs()
    return dir + filePath

}

/**
 * 获取 @RequestMapping 的 value 值
 * @param str
 * @return
 */
def getMappingValue(String str) {
    return str.replace("_", "")
}

/**
 * 驼峰转下划线
 * @param str
 */
def humpToUnderLine(String str) {
    String result = ""
    for (int i = 0; i < str.length(); i++) {
        char c = str.charAt(i);
        result += c >= 'A' && c <= 'Z' ? "_" + (char) (c + 32) : c
    }
    return result;
}
