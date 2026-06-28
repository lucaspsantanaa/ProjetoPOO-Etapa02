package clinica;

import java.util.List;
import java.util.Scanner;

import clinica.model.Atendimento;
import clinica.model.ClinicoGeral;
import clinica.model.Consulta;
import clinica.model.Convenio;
import clinica.model.Exportavel;
import clinica.model.Fisioterapeuta;
import clinica.model.HorarioDisponivel;
import clinica.model.Nutricionista;
import clinica.model.Pagamento;
import clinica.model.Paciente;
import clinica.model.Pessoa;
import clinica.model.Profissional;
import clinica.model.Psicologo;

import clinica.service.ClinicaServico;

import clinica.exception.ConsultaNaoEncontradaException;
import clinica.exception.ConvenioNaoCobreException;
import clinica.exception.HorarioIndisponivelException;
import clinica.exception.OperacaoInvalidaException;
import clinica.exception.PacienteInativoException;
import clinica.exception.PacienteNaoEncontradoException;
import clinica.exception.PagamentoInvalidoException;
import clinica.exception.ProfissionalNaoEncontradoException;

/**
 * Camada de APRESENTACAO (Etapa 13): apenas menus, leitura de dados e exibicao.
 * Toda a regra de negocio vive em ClinicaServico. A Main captura as excecoes
 * lancadas pelo servico (blocos catch SEPARADOS por tipo) e exibe mensagens
 * amigaveis, sem nunca encerrar a aplicacao abruptamente.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final ClinicaServico servico = new ClinicaServico();

    public static void main(String[] args) {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n===== CLINICA VIDAPLENA =====");
            System.out.println("1 - Pacientes");
            System.out.println("2 - Profissionais");
            System.out.println("3 - Consultas");
            System.out.println("4 - Atendimentos");
            System.out.println("5 - Pagamentos");
            System.out.println("6 - Relatorios");
            System.out.println("7 - Exportar dados");
            System.out.println("8 - Carregar dados de demonstracao");
            System.out.println("0 - Sair");
            opcao = lerInteiro("Escolha: ");

            switch (opcao) {
                case 1: menuPacientes(); break;
                case 2: menuProfissionais(); break;
                case 3: menuConsultas(); break;
                case 4: menuAtendimentos(); break;
                case 5: menuPagamentos(); break;
                case 6: menuRelatorios(); break;
                case 7: exportarDados(); break;
                case 8: carregarDemo(); break;
                case 0: break;
                default: System.out.println("Opcao invalida!"); break;
            }
        }
        System.out.println("Sistema encerrado.");
    }

    // ===================== LEITURA SEGURA DE ENTRADA =====================

    /**
     * Le um inteiro tratando NumberFormatException (excecao NAO verificada).
     * Se o usuario digitar algo invalido (ex.: "abc" na idade), exibe mensagem
     * amigavel e pede de novo, sem travar (cenario obrigatorio 1).
     */
    private static int lerInteiro(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = sc.nextLine();
            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("  >> Valor invalido. Digite um numero inteiro.");
            }
        }
    }

    private static double lerDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = sc.nextLine();
            try {
                return Double.parseDouble(entrada.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("  >> Valor invalido. Digite um numero (ex.: 150.00).");
            }
        }
    }

    private static String lerTexto(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    // ===================== PACIENTES =====================

    private static void menuPacientes() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- PACIENTES ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Complementar cadastro");
            System.out.println("3 - Buscar por CPF");
            System.out.println("4 - Listar todos");
            System.out.println("5 - Desativar");
            System.out.println("0 - Voltar");
            op = lerInteiro("Opcao: ");
            switch (op) {
                case 1: cadastrarPaciente(); break;
                case 2: complementarPaciente(); break;
                case 3: buscarPaciente(); break;
                case 4: listarPacientes(); break;
                case 5: desativarPaciente(); break;
                case 0: break;
                default: System.out.println("Opcao invalida!"); break;
            }
        }
    }

    private static void cadastrarPaciente() {
        String nome = lerTexto("Nome: ");
        String cpf = lerTexto("CPF: ");
        int modo = lerInteiro("Tipo (1-Minimo / 2-Idade+Tel / 3-Completo): ");
        try {
            Paciente p;
            if (modo == 2) {
                int idade = lerInteiro("Idade: ");
                String tel = lerTexto("Telefone: ");
                p = new Paciente(nome, cpf, idade, tel);
            } else if (modo == 3) {
                int idade = lerInteiro("Idade: ");
                String tel = lerTexto("Telefone: ");
                String nasc = lerTexto("Data nascimento (DD/MM/AAAA): ");
                Convenio conv = escolherConvenio();
                p = new Paciente(nome, cpf, idade, tel, nasc, conv);
            } else {
                p = new Paciente(nome, cpf);
            }
            servico.cadastrarPaciente(p);
            System.out.println("OK! " + p.exibirResumo());
        } catch (OperacaoInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Dados invalidos (nome/CPF vazio, idade negativa): nao trava o sistema.
            System.out.println("Dados invalidos: " + e.getMessage());
        }
    }

    private static void complementarPaciente() {
        String cpf = lerTexto("CPF: ");
        try {
            Paciente p = servico.buscarPacientePorCpf(cpf);
            int idade = lerInteiro("Idade: ");
            String tel = lerTexto("Telefone: ");
            String temConv = lerTexto("Informar convenio? (s/n): ");
            if (temConv.equalsIgnoreCase("s")) {
                p.complementar(idade, tel, escolherConvenio());
            } else {
                p.complementar(idade, tel);
            }
            System.out.println("Atualizado: " + p.exibirResumo());
        } catch (PacienteNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Dados invalidos: " + e.getMessage());
        }
    }

    private static void buscarPaciente() {
        String cpf = lerTexto("CPF: ");
        try {
            Paciente p = servico.buscarPacientePorCpf(cpf);
            System.out.println(p.exibirResumo());
        } catch (PacienteNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void listarPacientes() {
        List<Paciente> lista = servico.getPacientes();
        if (lista.isEmpty()) {
            System.out.println("Nenhum paciente cadastrado.");
            return;
        }
        // List: iteracao com for-each.
        for (Paciente p : lista) {
            System.out.println(p.exibirResumo());
        }
    }

    private static void desativarPaciente() {
        String cpf = lerTexto("CPF: ");
        try {
            servico.desativarPaciente(cpf);
            System.out.println("Paciente desativado.");
        } catch (PacienteNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static Convenio escolherConvenio() {
        List<Convenio> convs = servico.getConvenios();
        System.out.println("Convenios disponiveis:");
        for (int i = 0; i < convs.size(); i++) {
            Convenio c = convs.get(i);
            System.out.println("  " + (i + 1) + " - " + c.getNome()
                    + " (" + (int) c.getPercentualCobertura() + "%)");
        }
        System.out.println("  0 - Nenhum (particular)");
        int escolha = lerInteiro("Convenio: ");
        if (escolha >= 1 && escolha <= convs.size()) {
            return convs.get(escolha - 1);
        }
        return null;
    }

    // ===================== PROFISSIONAIS =====================

    private static void menuProfissionais() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- PROFISSIONAIS ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Adicionar horario disponivel");
            System.out.println("3 - Buscar por nome");
            System.out.println("4 - Listar todos");
            System.out.println("0 - Voltar");
            op = lerInteiro("Opcao: ");
            switch (op) {
                case 1: cadastrarProfissional(); break;
                case 2: adicionarHorario(); break;
                case 3: buscarProfissional(); break;
                case 4: listarProfissionais(); break;
                case 0: break;
                default: System.out.println("Opcao invalida!"); break;
            }
        }
    }

    private static void cadastrarProfissional() {
        String nome = lerTexto("Nome: ");
        String cpf = lerTexto("CPF: ");
        System.out.println("Especialidade: 1-Fisioterapia / 2-Psicologia / 3-Nutricao / 4-Clinica Geral");
        int esp = lerInteiro("Escolha: ");
        String registro = lerTexto("Registro profissional: ");
        double valor = lerDouble("Valor da consulta: ");
        try {
            Profissional prof;
            if (esp == 1) {
                int sessoes = lerInteiro("Total de sessoes previstas: ");
                prof = new Fisioterapeuta(nome, cpf, registro, valor, sessoes);
            } else if (esp == 2) {
                String abordagem = lerTexto("Abordagem terapeutica (ex.: TCC): ");
                prof = new Psicologo(nome, cpf, registro, valor, abordagem);
            } else if (esp == 3) {
                String plano = lerTexto("Plano alimentar: ");
                prof = new Nutricionista(nome, cpf, registro, valor, plano);
            } else {
                String enc = lerTexto("Encaminhamento padrao: ");
                prof = new ClinicoGeral(nome, cpf, registro, valor, enc);
            }
            servico.cadastrarProfissional(prof);
            System.out.println("OK! " + prof.exibirResumo());
        } catch (OperacaoInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Dados invalidos: " + e.getMessage());
        }
    }

    private static void adicionarHorario() {
        String nome = lerTexto("Nome do profissional: ");
        String dia = lerTexto("Dia da semana (segunda/terca/quarta/quinta/sexta/sabado/domingo): ");
        String turno = lerTexto("Turno (manha/tarde): ");
        try {
            servico.adicionarHorario(nome, new HorarioDisponivel(dia, turno));
            System.out.println("Horario adicionado.");
        } catch (ProfissionalNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void buscarProfissional() {
        String nome = lerTexto("Nome: ");
        try {
            Profissional p = servico.buscarProfissionalPorNome(nome);
            System.out.println(p.exibirResumo());
            System.out.println("  Horarios: " + p.getHorarios());
        } catch (ProfissionalNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void listarProfissionais() {
        List<Profissional> lista = servico.getProfissionais();
        if (lista.isEmpty()) {
            System.out.println("Nenhum profissional cadastrado.");
            return;
        }
        for (Profissional p : lista) {
            System.out.println(p.exibirResumo());
        }
    }

    // ===================== CONSULTAS =====================

    private static void menuConsultas() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- CONSULTAS ---");
            System.out.println("1 - Agendar (escolher profissional)");
            System.out.println("2 - Agendar (busca por especialidade)");
            System.out.println("3 - Cancelar");
            System.out.println("4 - Remarcar");
            System.out.println("5 - Listar todas");
            System.out.println("6 - Buscar (CPF + data + horario)");
            System.out.println("0 - Voltar");
            op = lerInteiro("Opcao: ");
            switch (op) {
                case 1: agendarComProfissional(); break;
                case 2: agendarPorEspecialidade(); break;
                case 3: cancelarConsulta(); break;
                case 4: remarcarConsulta(); break;
                case 5: listarConsultas(); break;
                case 6: buscarConsulta(); break;
                case 0: break;
                default: System.out.println("Opcao invalida!"); break;
            }
        }
    }

    private static void agendarComProfissional() {
        String cpf = lerTexto("CPF do paciente: ");
        String nomeProf = lerTexto("Nome do profissional: ");
        String data = lerTexto("Data (DD/MM/AAAA): ");
        String horario = lerTexto("Horario (HH:MM): ");
        String tipo = lerTexto("Tipo (inicial/retorno/avaliacao): ");
        String dia = descobrirDiaSemana(data);
        Consulta c = tentarAgendar(cpf, nomeProf, data, horario, tipo, dia, true);
        if (c != null) {
            System.out.println("Consulta agendada: " + c.exibirResumo());
        }
    }

    /**
     * Tenta agendar e trata cada excecao em bloco catch SEPARADO. Em caso de
     * conflito de horario (HorarioIndisponivel), oferece um horario alternativo
     * (jornadas 7 e 19). O parametro oferecerSugestao evita recursao infinita.
     */
    private static Consulta tentarAgendar(String cpf, String nomeProf, String data,
                                          String horario, String tipo, String dia,
                                          boolean oferecerSugestao) {
        try {
            return servico.agendarConsulta(cpf, nomeProf, data, horario, tipo, dia);
        } catch (PacienteNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (PacienteInativoException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (ProfissionalNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (HorarioIndisponivelException e) {
            System.out.println("Erro: " + e.getMessage());
            if (oferecerSugestao) {
                String sugestao = servico.sugerirHorario(nomeProf, data);
                if (!sugestao.isEmpty()) {
                    String aceita = lerTexto("Horario livre sugerido: " + sugestao + ". Aceitar? (s/n): ");
                    if (aceita.equalsIgnoreCase("s")) {
                        return tentarAgendar(cpf, nomeProf, data, sugestao, tipo, dia, false);
                    }
                } else {
                    System.out.println("Nenhum horario livre nesse dia.");
                }
            }
        }
        return null;
    }

    private static void agendarPorEspecialidade() {
        String cpf = lerTexto("CPF do paciente: ");
        String esp = lerTexto("Especialidade (fisioterapia/psicologia/nutricao/clinica geral): ");
        String data = lerTexto("Data (DD/MM/AAAA): ");
        String horario = lerTexto("Horario (HH:MM): ");
        String dia = descobrirDiaSemana(data);
        try {
            Consulta c = servico.agendarPorEspecialidade(cpf, esp, data, horario, dia);
            System.out.println("Consulta agendada com " + c.getProfissional().getNome()
                    + ": " + c.exibirResumo());
        } catch (PacienteNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (PacienteInativoException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (ProfissionalNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (HorarioIndisponivelException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void cancelarConsulta() {
        String cpf = lerTexto("CPF: ");
        String data = lerTexto("Data (DD/MM/AAAA): ");
        String horario = lerTexto("Horario (HH:MM): ");
        int antecedencia = lerInteiro("Horas de antecedencia ate a consulta: ");
        String motivo = lerTexto("Motivo: ");
        try {
            String msg = servico.cancelarConsulta(cpf, data, horario, motivo, antecedencia);
            System.out.println(msg);
        } catch (ConsultaNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (OperacaoInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void remarcarConsulta() {
        String cpf = lerTexto("CPF: ");
        String data = lerTexto("Data original (DD/MM/AAAA): ");
        String horario = lerTexto("Horario original (HH:MM): ");
        String novaData = lerTexto("Nova data (DD/MM/AAAA): ");
        String novoHorario = lerTexto("Novo horario (HH:MM): ");
        String novoDia = descobrirDiaSemana(novaData);
        try {
            Consulta nova = servico.remarcarConsulta(cpf, data, horario, novaData, novoHorario, novoDia);
            System.out.println("Remarcada: " + nova.exibirResumo());
        } catch (ConsultaNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (OperacaoInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (HorarioIndisponivelException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void listarConsultas() {
        List<Consulta> lista = servico.getConsultas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma consulta.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("[" + i + "] " + lista.get(i).exibirResumo());
        }
    }

    private static void buscarConsulta() {
        String cpf = lerTexto("CPF: ");
        String data = lerTexto("Data (DD/MM/AAAA): ");
        String horario = lerTexto("Horario (HH:MM): ");
        try {
            Consulta c = servico.buscarConsulta(cpf, data, horario);
            System.out.println(c.exibirResumo());
        } catch (ConsultaNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ===================== ATENDIMENTOS =====================

    private static void menuAtendimentos() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- ATENDIMENTOS ---");
            System.out.println("1 - Registrar atendimento");
            System.out.println("2 - Listar atendimentos");
            System.out.println("0 - Voltar");
            op = lerInteiro("Opcao: ");
            switch (op) {
                case 1: registrarAtendimento(); break;
                case 2: listarAtendimentos(); break;
                case 0: break;
                default: System.out.println("Opcao invalida!"); break;
            }
        }
    }

    private static void registrarAtendimento() {
        String cpf = lerTexto("CPF do paciente: ");
        String data = lerTexto("Data da consulta (DD/MM/AAAA): ");
        String horario = lerTexto("Horario da consulta (HH:MM): ");
        try {
            Consulta consulta = servico.buscarConsulta(cpf, data, horario);
            String obs = lerTexto("Observacoes: ");
            String diag = lerTexto("Diagnostico: ");
            Atendimento at = servico.registrarAtendimento(consulta, obs, diag);
            String add = lerTexto("Adicionar procedimento? (s/n): ");
            while (add.equalsIgnoreCase("s")) {
                at.adicionarProcedimento(lerTexto("Procedimento: "));
                add = lerTexto("Adicionar outro? (s/n): ");
            }
            System.out.println("\n--- RESUMO DO ATENDIMENTO ---");
            System.out.println(at.exibirResumo());
        } catch (ConsultaNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (OperacaoInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            // finally com proposito real (R9): log de encerramento da operacao.
            System.out.println("--- Registro de atendimento finalizado ---");
        }
    }

    private static void listarAtendimentos() {
        List<Atendimento> lista = servico.getAtendimentos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum atendimento registrado.");
            return;
        }
        for (Atendimento a : lista) {
            System.out.println(a.exibirResumo());
            System.out.println("---");
        }
    }

    // ===================== PAGAMENTOS =====================

    private static void menuPagamentos() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- PAGAMENTOS ---");
            System.out.println("1 - Registrar pagamento");
            System.out.println("2 - Listar pagamentos");
            System.out.println("0 - Voltar");
            op = lerInteiro("Opcao: ");
            switch (op) {
                case 1: registrarPagamento(); break;
                case 2: listarPagamentos(); break;
                case 0: break;
                default: System.out.println("Opcao invalida!"); break;
            }
        }
    }

    private static void registrarPagamento() {
        String cpf = lerTexto("CPF do paciente: ");
        String data = lerTexto("Data da consulta (DD/MM/AAAA): ");
        String horario = lerTexto("Horario da consulta (HH:MM): ");
        try {
            Consulta consulta = servico.buscarConsulta(cpf, data, horario);
            System.out.println("Valor base da consulta: R$"
                    + String.format("%.2f", consulta.getProfissional().getValorConsulta()));
            String tipo = lerTexto("Forma (dinheiro/pix/cartao/convenio): ");
            int parcelas = 1;
            if (tipo.equalsIgnoreCase("cartao")) {
                parcelas = lerInteiro("Parcelas (1 a 6): ");
            }
            Pagamento pag = servico.processarPagamento(consulta, tipo, parcelas);
            System.out.println("Pagamento registrado: " + pag.exibirResumo());
        } catch (ConsultaNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (PagamentoInvalidoException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (ConvenioNaoCobreException e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            // finally com proposito real (R9): exigido pelo enunciado.
            System.out.println("--- Operacao de pagamento finalizada ---");
        }
    }

    private static void listarPagamentos() {
        List<Pagamento> lista = servico.getPagamentos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum pagamento registrado.");
            return;
        }
        for (Pagamento p : lista) {
            System.out.println(p.exibirResumo());
        }
    }

    // ===================== RELATORIOS =====================

    private static void menuRelatorios() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- RELATORIOS ---");
            System.out.println("1 - Unificado de cadastros (polimorfismo)");
            System.out.println("2 - Pagamentos (polimorfismo)");
            System.out.println("3 - Financeiro");
            System.out.println("4 - Por profissional");
            System.out.println("5 - Por periodo");
            System.out.println("0 - Voltar");
            op = lerInteiro("Opcao: ");
            switch (op) {
                case 1: relatorioUnificado(); break;
                case 2: relatorioPagamentos(); break;
                case 3: relatorioFinanceiro(); break;
                case 4: relatorioPorProfissional(); break;
                case 5: relatorioPorPeriodo(); break;
                case 0: break;
                default: System.out.println("Opcao invalida!"); break;
            }
        }
    }

    /**
     * RELATORIO UNIFICADO (jornada 15): percorre uma List<Pessoa> e chama
     * exibirResumo() em cada elemento.
     *
     * LIGACAO DINAMICA: o metodo executado depende do tipo REAL do objeto
     * (Paciente, Fisioterapeuta, Psicologo...), nao do tipo da referencia (Pessoa).
     *
     * DYNAMIC CASTING: usa instanceof para identificar o tipo real e fazer um
     * cast seguro, exibindo informacoes extras especificas de cada categoria.
     */
    private static void relatorioUnificado() {
        List<Pessoa> pessoas = servico.getTodasPessoas();
        if (pessoas.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada.");
            return;
        }
        System.out.println("\n=== RELATORIO UNIFICADO DE CADASTROS ===");
        int totalPacientes = 0;
        int totalProfissionais = 0;
        for (Pessoa p : pessoas) {
            System.out.println(p.exibirResumo()); // ligacao dinamica
            if (p instanceof Paciente) {
                totalPacientes++;
                Paciente pac = (Paciente) p; // cast seguro apos instanceof
                if (pac.getConvenio() != null) {
                    System.out.println("    -> Convenio: " + pac.getConvenio().getNome());
                }
            } else if (p instanceof Profissional) {
                totalProfissionais++;
                Profissional prof = (Profissional) p; // cast seguro apos instanceof
                System.out.println("    -> Registro: " + prof.getRegistroProfissional()
                        + " | Atende em " + prof.getHorarios().size() + " horario(s)");
            }
        }
        System.out.println("Total de pacientes: " + totalPacientes);
        System.out.println("Total de profissionais: " + totalProfissionais);
    }

    /**
     * RELATORIO DE PAGAMENTOS: percorre uma List<Pagamento> e chama
     * calcularValorFinal() em cada um. Cada subclasse retorna um valor diferente
     * (dinheiro, cartao, convenio) -> polimorfismo via ligacao dinamica.
     */
    private static void relatorioPagamentos() {
        List<Pagamento> pags = servico.getPagamentos();
        if (pags.isEmpty()) {
            System.out.println("Nenhum pagamento registrado.");
            return;
        }
        System.out.println("\n=== RELATORIO DE PAGAMENTOS ===");
        double total = 0;
        for (Pagamento pag : pags) {
            double valor = pag.calcularValorFinal(); // ligacao dinamica
            total += valor;
            System.out.println(pag.exibirResumo());
        }
        System.out.println("Total recebido: R$" + String.format("%.2f", total));
    }

    private static void relatorioFinanceiro() {
        int realizadas = 0;
        int canceladas = 0;
        double totalMultas = 0;
        for (Consulta c : servico.getConsultas()) {
            if (c.getStatus().equals("realizada")) {
                realizadas++;
            }
            if (c.getStatus().equals("cancelada")) {
                canceladas++;
            }
            totalMultas += c.getMulta();
        }
        double totalRecebido = 0;
        for (Pagamento p : servico.getPagamentos()) {
            totalRecebido += p.calcularValorFinal();
        }
        System.out.println("\n=== RESUMO FINANCEIRO ===");
        System.out.println("Consultas realizadas: " + realizadas);
        System.out.println("Consultas canceladas: " + canceladas);
        System.out.println("Total recebido: R$" + String.format("%.2f", totalRecebido));
        System.out.println("Total em multas: R$" + String.format("%.2f", totalMultas));
    }

    private static void relatorioPorProfissional() {
        String nome = lerTexto("Nome do profissional: ");
        System.out.println("\n=== CONSULTAS DE " + nome + " ===");
        boolean achou = false;
        for (Consulta c : servico.getConsultas()) {
            if (c.getProfissional().getNome().equalsIgnoreCase(nome)) {
                System.out.println(c.exibirResumo());
                achou = true;
            }
        }
        if (!achou) {
            System.out.println("Nenhuma consulta encontrada para esse profissional.");
        }
    }

    private static void relatorioPorPeriodo() {
        String inicio = lerTexto("Data inicio (DD/MM/AAAA): ");
        String fim = lerTexto("Data fim (DD/MM/AAAA): ");
        System.out.println("\n=== CONSULTAS DE " + inicio + " A " + fim + " ===");
        for (Consulta c : servico.getConsultas()) {
            if (estaNoIntervalo(c.getData(), inicio, fim)) {
                System.out.println(c.exibirResumo());
            }
        }
    }

    // ===================== EXPORTACAO =====================

    /**
     * EXPORTACAO (jornada 26): percorre todos os objetos Exportaveis (consultas,
     * atendimentos e pagamentos) usando uma unica List<Exportavel> e chama
     * exportarDados() em cada um -> polimorfismo via INTERFACE. O codigo nao
     * precisa saber o tipo concreto do objeto para exporta-lo.
     */
    private static void exportarDados() {
        List<Exportavel> itens = servico.getExportaveis();
        if (itens.isEmpty()) {
            System.out.println("Nada para exportar.");
            return;
        }
        System.out.println("\n=== EXPORTACAO DE DADOS ===");
        for (Exportavel e : itens) {
            System.out.println(e.exportarDados());
        }
    }

    // ===================== DADOS DE DEMONSTRACAO =====================

    private static void carregarDemo() {
        try {
            Paciente maria = new Paciente("Maria Silva", "111", 30, "83999990000",
                    "10/05/1995", servico.buscarConvenio("SaudePlus"));
            Paciente joao = new Paciente("Joao Souza", "222");
            servico.cadastrarPaciente(maria);
            servico.cadastrarPaciente(joao);

            Fisioterapeuta fisio = new Fisioterapeuta("Ana Fisio", "333", "CREFITO-123", 150.0, 12);
            Psicologo psi = new Psicologo("Carlos Psi", "444", "CRP-456", 200.0, "TCC");
            Nutricionista nutri = new Nutricionista("Bia Nutri", "555", "CRN-789", 180.0, "Low carb");
            ClinicoGeral clinico = new ClinicoGeral("Dr House", "666", "CRM-000", 250.0, "Cardiologia");
            servico.cadastrarProfissional(fisio);
            servico.cadastrarProfissional(psi);
            servico.cadastrarProfissional(nutri);
            servico.cadastrarProfissional(clinico);

            // Horarios (agregacao): disponibilizam todos os dias uteis para facilitar o teste.
            String[] diasUteis = {"segunda", "terca", "quarta", "quinta", "sexta"};
            for (String d : diasUteis) {
                fisio.adicionarHorario(new HorarioDisponivel(d, "manha"));
                psi.adicionarHorario(new HorarioDisponivel(d, "manha"));
                nutri.adicionarHorario(new HorarioDisponivel(d, "tarde"));
                clinico.adicionarHorario(new HorarioDisponivel(d, "manha"));
            }

            System.out.println("Dados de demonstracao carregados:");
            System.out.println("  Pacientes -> CPF 111 (Maria, convenio SaudePlus) e CPF 222 (Joao, particular)");
            System.out.println("  Profissionais -> Ana Fisio, Carlos Psi, Bia Nutri, Dr House (atendem seg-sex)");
            System.out.println("  Convenios -> SaudePlus (40%), VidaMais (30%), BemEstar (50%)");
        } catch (OperacaoInvalidaException e) {
            System.out.println("Demo provavelmente ja carregada: " + e.getMessage());
        }
    }

    // ===================== UTILITARIOS DE DATA =====================

    // Descobre o dia da semana a partir da data (formula de Zeller).
    private static String descobrirDiaSemana(String data) {
        try {
            int dia = Integer.parseInt(data.substring(0, 2));
            int mes = Integer.parseInt(data.substring(3, 5));
            int ano = Integer.parseInt(data.substring(6, 10));
            if (mes < 3) {
                mes += 12;
                ano -= 1;
            }
            int k = ano % 100;
            int j = ano / 100;
            int r = (dia + (13 * (mes + 1)) / 5 + k + k / 4 + j / 4 - 2 * j) % 7;
            if (r < 0) {
                r += 7;
            }
            String[] nomes = {"sabado", "domingo", "segunda", "terca", "quarta", "quinta", "sexta"};
            return nomes[r];
        } catch (RuntimeException e) {
            // Data mal formatada: devolve algo que nao bate com nenhum dia cadastrado.
            return "invalido";
        }
    }

    private static boolean estaNoIntervalo(String data, String inicio, String fim) {
        return converter(data) >= converter(inicio) && converter(data) <= converter(fim);
    }

    private static int converter(String data) {
        try {
            int dia = Integer.parseInt(data.substring(0, 2));
            int mes = Integer.parseInt(data.substring(3, 5));
            int ano = Integer.parseInt(data.substring(6, 10));
            return ano * 10000 + mes * 100 + dia;
        } catch (RuntimeException e) {
            return -1;
        }
    }
}
