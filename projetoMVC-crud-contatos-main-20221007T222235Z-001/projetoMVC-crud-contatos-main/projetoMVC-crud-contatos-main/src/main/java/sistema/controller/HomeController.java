package sistema.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sistema.model.Contato;
import sistema.model.Professor;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @Autowired
    JdbcTemplate db;

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/contatos")
    public String contatos(Model model) {
        List<Contato> listaDeContatos = db.query(
                "select * from contatos",
                (res, rowNum) -> {
                    Contato contato = new Contato(
                            res.getInt("id"),
                            res.getString("nome"),
                            res.getString("telefone"),
                            res.getString("endereco"));
                    return contato;
                });
        model.addAttribute("contatos", listaDeContatos);
        return "contato";
    }

    @GetMapping("novo")
    public String exibeForm(Model model) {
        model.addAttribute("contato", new Contato());
        return "formulario";
    }

    @PostMapping("novoProfessor")
    public String gravaDados(Professor pro) {
        System.out.println("-----------------------");
        System.out.println(pro.getNome());

        db.update("insert into professores(nome) values (?)",
                pro.getNome());
        return "home";
    }

    @PostMapping("novo")
    public String gravaDados(Contato contato) {
        System.out.println("-----------------------");
        System.out.println(contato.getNome());
        System.out.println(contato.getEndereco());
        System.out.println(contato.getTelefone());

        db.update("insert into contatos(endereco, telefone, nome) values (?, ?, ?)",
                contato.getEndereco(), contato.getTelefone(), contato.getNome());
        return "home";
    }

    @GetMapping("/professores")
    public String professores(Model model) {
        List<Professor> listaDeProfessores = db.query(
                "select * from professores",
                (res, rowNum) -> {
                    Professor pro = new Professor(
                            res.getInt("id"),
                            res.getString("nome"));
                    return pro;
                });
        model.addAttribute("professores", listaDeProfessores);
        return "professor";
    }

    @GetMapping("novoProfessor")
    public String exibeCadastro(Model model) {
        model.addAttribute("professor", new Professor());
        return "cadProfessor";
    }

    @GetMapping("excluir-pro")
    public String apagarPro(@RequestParam(value = "id", required = true) Integer cod) {
        System.out.println("--------------------> " + cod);
        db.update("delete from professores where id = ?", cod);
        return "redirect:/professores";
    }

    @GetMapping("editar-pro")
    public String mostraProfessorAlterador(@RequestParam(value = "id", required = true) Integer cod, Model model) {
        System.out.println("--------------------> " + cod);
        Professor professor = db.queryForObject(
                "select * from professores where id = ?",
                (rs, rowNum) -> {
                    Professor pro = new Professor();
                    pro.setId(rs.getInt("id"));
                    pro.setNome(rs.getString("nome"));
                    return pro;
                },
                cod);
        model.addAttribute("pro", professor);
        return "editado";
    }

    @PostMapping("gravacontatomodificado")
    public String gravaProModificado(Professor professor) {
        db.update(
                "update professores set nome=? where id = ?",
                professor.getNome(),
                professor.getId());
        return "redirect:/professores";
    }
}
