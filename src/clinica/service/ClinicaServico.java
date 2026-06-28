package clinica.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import clinica.model.Atendimento;
import clinica.model.Consulta;
import clinica.model.Convenio;
import clinica.model.HorarioDisponivel;
import clinica.model.Pagamento;
import clinica.model.PagamentoCartao;
import clinica.model.PagamentoConvenio;
import clinica.model.PagamentoDinheiro;
import clinica.model.Paciente;
import clinica.model.Pessoa;
import clinica.model.Profissional;

import clinica.exception.ConsultaNaoEncontradaException;
import clinica.exception.ConvenioNaoCobreException;
import clinica.exception.HorarioIndisponivelException;
import clinica.exception.OperacaoInvalidaException;
import clinica.exception.PacienteInativoException;
import clinica.exception.PacienteNaoEncontradoException;
import clinica.exception.PagamentoInvalidoException;
import clinica.exception.ProfissionalNaoEncontradoException;

/**
 * ClinicaServico: concentra TODA a regra de negocio do sistema (Etapa 13).
 *
 * A Main cuida apenas de menus, leitura e exibicao; aqui ficam as operacoes,
 * as colecoes e o lancamento das excecoes de negocio (throws especificos).
 *
 * COLECOES (R10) - escolha justificada de cada estrutura:
 *  - ArrayList: para pacientes, profissionais, consultas, atendimentos,
 *    pagamentos e a lista unificada de pessoas. Ordem de insercao importa e
 *    precisamos iterar e acessar por indice.
 *  - HashSet: para o controle de CPFs ja cadastrados. So interessa saber se
 *    existe (contains) e impedir duplicata; nao ha ordem nem indice.
 *  - HashMap: para busca rapida por chave (CPF -> Paciente e nome ->
 *    Profissional), O(1) em vez de varrer a lista inteira.
 */
public class ClinicaServico {

    // ArrayList<...>: ordem de insercao importa; iteramos e acessamos por indice.
    private final List<Paciente> pacientes = new ArrayList<>();
    private final List<Profissional> profissionais = new ArrayList<>();
    private final List<Consulta> consultas = new ArrayList<>();
    private final List<Atendimento> atendimentos = new ArrayList<>();
    private final List<Pagamento> pagamentos = new ArrayList<>();
    private final List<Convenio> convenios = new ArrayList<>();

    // Lista UNIFICADA de pessoas (Paciente e Profissional juntos) usada no
    // relatorio polimorfico (List<Pessoa> -> exibirResumo()).
    private final List<Pessoa> todasPessoas = new ArrayList<>();

    // HashSet<String>: apenas verificacao de existencia de CPF (contains);
    // nao precisa de ordem. Garante unicidade antes de inserir.
    private final Set<String> cpfsCadastrados = new HashSet<>();

    // HashMap<String, Paciente>: busca por chave (CPF). Mais eficiente que
    // percorrer a lista de pacientes.
    private final Map<String, Paciente> mapaPacientesPorCpf = new HashMap<>();

    // HashMap<String, Profissional>: busca de profissional por nome.
    private final Map<String, Profissional> mapaProfissionaisPorNome = new HashMap<>();

    public ClinicaServico() {
        // Convenios pre-cadastrados (existem independentemente dos pacientes).
        criarConvenio("SaudePlus", 40, new String[]{"clinica geral", "fisioterapia", "psicologia", "nutricao"});
        criarConvenio("VidaMais", 30, new String[]{"clinica geral", "fisioterapia"});
        criarConvenio("BemEstar", 50, new String[]{"psicologia", "nutricao"});
    }

    private void criarConvenio(String nome, double cobertura, String[] especialidades) {
        List<String> lista = new ArrayList<>();
        for (String e : especialidades) {
            lista.add(e.toLowerCase());
        }
        convenios.add(new Convenio(nome, cobertura, lista));
    }

    public Convenio buscarConvenio(String nome) {
        for (Convenio c : convenios) {
            if (c.getNome().equalsIgnoreCase(nome)) {
                return c;
            }
        }
        return null;
    }

    public List<Convenio> getConvenios() {
        return convenios;
    }

    // ================= PACIENTES =================

    /**
     * Cadastra um paciente garantindo unicidade de CPF via HashSet.
     * Demonstra Set.add() retornando false ao tentar inserir duplicata.
     */
    public void cadastrarPaciente(Paciente paciente) throws OperacaoInvalidaException {
        String cpf = paciente.getCpf();
        // Set.add retorna false se o elemento ja existia -> CPF duplicado.
        boolean novo = cpfsCadastrados.add(cpf);
        if (!novo) {
            throw new OperacaoInvalidaException("CPF ja cadastrado no sistema: " + cpf);
        }
        pacientes.add(paciente);
        todasPessoas.add(paciente);
        mapaPacientesPorCpf.put(cpf, paciente); // Map.put: chave CPF -> Paciente
    }

    /**
     * Busca paciente por CPF usando o HashMap (O(1)).
     * Demonstra Map.containsKey()/get(). Lanca excecao se nao existir.
     */
    public Paciente buscarPacientePorCpf(String cpf) throws PacienteNaoEncontradoException {
        if (!mapaPacientesPorCpf.containsKey(cpf)) {
            throw new PacienteNaoEncontradoException("Paciente com CPF " + cpf + " nao encontrado.");
        }
        return mapaPacientesPorCpf.get(cpf);
    }

    public void desativarPaciente(String cpf) throws PacienteNaoEncontradoException {
        Paciente p = buscarPacientePorCpf(cpf);
        p.desativar();
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    // ================= PROFISSIONAIS =================

    /**
     * Cadastra um profissional, controlando unicidade de CPF (Set) e indexando
     * por nome no HashMap para busca rapida.
     */
    public void cadastrarProfissional(Profissional profissional) throws OperacaoInvalidaException {
        String cpf = profissional.getCpf();
        if (!cpfsCadastrados.add(cpf)) {
            throw new OperacaoInvalidaException("CPF ja cadastrado no sistema: " + cpf);
        }
        if (mapaProfissionaisPorNome.containsKey(profissional.getNome())) {
            cpfsCadastrados.remove(cpf); // desfaz para manter consistencia
            throw new OperacaoInvalidaException(
                    "Ja existe um profissional com o nome: " + profissional.getNome());
        }
        profissionais.add(profissional);
        todasPessoas.add(profissional);
        mapaProfissionaisPorNome.put(profissional.getNome(), profissional);
    }

    /**
     * Busca profissional por nome usando o HashMap. Lanca excecao se nao existir.
     */
    public Profissional buscarProfissionalPorNome(String nome) throws ProfissionalNaoEncontradoException {
        if (!mapaProfissionaisPorNome.containsKey(nome)) {
            throw new ProfissionalNaoEncontradoException("Profissional '" + nome + "' nao encontrado.");
        }
        return mapaProfissionaisPorNome.get(nome);
    }

    public void adicionarHorario(String nomeProfissional, HorarioDisponivel horario)
            throws ProfissionalNaoEncontradoException {
        Profissional prof = buscarProfissionalPorNome(nomeProfissional);
        prof.adicionarHorario(horario);
    }

    public List<Profissional> getProfissionais() {
        return profissionais;
    }

    // ================= CONSULTAS =================

    /**
     * Agenda consulta escolhendo o profissional pelo nome.
     * Declara TODAS as excecoes especificas que podem ocorrer (sem throws Exception
     * generico): paciente inexistente/inativo, profissional inexistente, horario
     * indisponivel.
     */
    public Consulta agendarConsulta(String cpfPaciente, String nomeProfissional,
                                    String data, String horario, String tipo, String diaSemana)
            throws PacienteNaoEncontradoException, PacienteInativoException,
                   ProfissionalNaoEncontradoException, HorarioIndisponivelException {

        Paciente paciente = buscarPacientePorCpf(cpfPaciente); // pode lancar PacienteNaoEncontrado

        if (!paciente.isAtivo()) {
            throw new PacienteInativoException(
                    "Paciente " + paciente.getNome() + " esta inativo. Nao e possivel agendar.");
        }

        Profissional prof = buscarProfissionalPorNome(nomeProfissional); // pode lancar ProfissionalNaoEncontrado

        if (!prof.atendeNoDia(diaSemana)) {
            throw new HorarioIndisponivelException(
                    prof.getNome() + " nao atende em " + diaSemana + ".");
        }

        if (temConflito(prof, data, horario)) {
            throw new HorarioIndisponivelException(
                    "Horario " + horario + " ja ocupado para " + prof.getNome() + " em " + data + ".");
        }

        Consulta consulta = new Consulta(paciente, prof, data, horario, tipo);
        consultas.add(consulta);
        return consulta;
    }

    /**
     * Agenda por especialidade: localiza automaticamente um profissional
     * disponivel da especialidade pedida no dia/horario.
     */
    public Consulta agendarPorEspecialidade(String cpfPaciente, String especialidade,
                                            String data, String horario, String diaSemana)
            throws PacienteNaoEncontradoException, PacienteInativoException,
                   ProfissionalNaoEncontradoException, HorarioIndisponivelException {

        Paciente paciente = buscarPacientePorCpf(cpfPaciente);
        if (!paciente.isAtivo()) {
            throw new PacienteInativoException(
                    "Paciente " + paciente.getNome() + " esta inativo. Nao e possivel agendar.");
        }

        for (Profissional prof : profissionais) {
            if (prof.getEspecialidade().equalsIgnoreCase(especialidade)
                    && prof.atendeNoDia(diaSemana)
                    && !temConflito(prof, data, horario)) {
                Consulta consulta = new Consulta(paciente, prof, data, horario);
                consultas.add(consulta);
                return consulta;
            }
        }
        throw new ProfissionalNaoEncontradoException(
                "Nenhum profissional de " + especialidade + " disponivel em " + data + " " + horario + ".");
    }

    /**
     * Localiza uma consulta por CPF + data + horario. Lanca excecao se nao achar.
     */
    public Consulta buscarConsulta(String cpf, String data, String horario)
            throws ConsultaNaoEncontradaException {
        for (Consulta c : consultas) {
            if (c.getPaciente().getCpf().equals(cpf)
                    && c.getData().equals(data)
                    && c.getHorario().equals(horario)) {
                return c;
            }
        }
        throw new ConsultaNaoEncontradaException(
                "Consulta nao encontrada para CPF " + cpf + " em " + data + " " + horario + ".");
    }

    /**
     * Cancela consulta aplicando regra de multa por antecedencia.
     * Bloqueia cancelar consulta ja realizada/cancelada (OperacaoInvalida).
     */
    public String cancelarConsulta(String cpf, String data, String horario,
                                   String motivo, int horasAntecedencia)
            throws ConsultaNaoEncontradaException, OperacaoInvalidaException {

        Consulta consulta = buscarConsulta(cpf, data, horario);

        if (consulta.getStatus().equals("realizada")) {
            throw new OperacaoInvalidaException("Consulta ja realizada nao pode ser cancelada.");
        }
        if (consulta.getStatus().equals("cancelada")) {
            throw new OperacaoInvalidaException("Consulta ja esta cancelada.");
        }

        String resultado;
        if (horasAntecedencia < 2) {
            consulta.setMulta(50.0);
            resultado = "Cancelamento com menos de 2h de antecedencia: multa de R$50,00 aplicada. ";
        } else {
            resultado = "Cancelamento dentro do prazo, sem multa. ";
        }
        resultado += consulta.cancelar(motivo);
        return resultado;
    }

    /**
     * Remarca: marca a consulta original como remarcada e cria uma nova consulta
     * no novo horario (preservando o historico).
     */
    public Consulta remarcarConsulta(String cpf, String dataOrig, String horarioOrig,
                                     String novaData, String novoHorario, String novoDiaSemana)
            throws ConsultaNaoEncontradaException, OperacaoInvalidaException,
                   HorarioIndisponivelException {

        Consulta original = buscarConsulta(cpf, dataOrig, horarioOrig);

        if (!original.getStatus().equals("agendada")) {
            throw new OperacaoInvalidaException(
                    "So consultas agendadas podem ser remarcadas (status atual: " + original.getStatus() + ").");
        }

        Profissional prof = original.getProfissional();
        if (!prof.atendeNoDia(novoDiaSemana)) {
            throw new HorarioIndisponivelException(prof.getNome() + " nao atende em " + novoDiaSemana + ".");
        }
        if (temConflito(prof, novaData, novoHorario)) {
            throw new HorarioIndisponivelException("Novo horario ja ocupado para " + prof.getNome() + ".");
        }

        original.remarcar();
        Consulta nova = new Consulta(original.getPaciente(), prof, novaData, novoHorario, original.getTipo());
        consultas.add(nova);
        return nova;
    }

    // Metodo auxiliar PRIVADO (R2: metodos auxiliares internos sao privados).
    private boolean temConflito(Profissional prof, String data, String horario) {
        for (Consulta c : consultas) {
            if (c.getProfissional() == prof
                    && c.getData().equals(data)
                    && c.getHorario().equals(horario)
                    && c.getStatus().equals("agendada")) {
                return true;
            }
        }
        return false;
    }

    // Sugere o proximo horario livre (08h-18h) para o profissional naquele dia.
    public String sugerirHorario(String nomeProfissional, String data) {
        Profissional prof = mapaProfissionaisPorNome.get(nomeProfissional);
        if (prof == null) {
            return "";
        }
        for (int h = 8; h <= 18; h++) {
            String teste = (h < 10 ? "0" + h : "" + h) + ":00";
            if (!temConflito(prof, data, teste)) {
                return teste;
            }
        }
        return "";
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    // ================= ATENDIMENTOS =================

    /**
     * Registra atendimento de uma consulta agendada. Cria o Atendimento (que por
     * composicao cria o Prontuario) e dispara registrarEspecifico() do
     * profissional (polimorfismo: cada especialidade adiciona sua informacao).
     */
    public Atendimento registrarAtendimento(Consulta consulta, String observacoes, String diagnostico)
            throws OperacaoInvalidaException {

        if (!consulta.getStatus().equals("agendada")) {
            throw new OperacaoInvalidaException(
                    "So e possivel registrar atendimento em consulta agendada (status: "
                            + consulta.getStatus() + ").");
        }

        Atendimento atendimento = new Atendimento(consulta, consulta.getProfissional(), observacoes, diagnostico);

        // POLIMORFISMO + LIGACAO DINAMICA: o tipo real do profissional decide o
        // que e registrado (sessoes, abordagem, plano alimentar, encaminhamento).
        consulta.getProfissional().registrarEspecifico(atendimento);

        consulta.realizar();
        atendimentos.add(atendimento);
        return atendimento;
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    // ================= PAGAMENTOS =================

    /**
     * Processa um pagamento de forma POLIMORFICA: instancia a subclasse correta
     * de Pagamento conforme o tipo. Valida tipo, parcelas e cobertura de convenio,
     * lancando as excecoes especificas (PagamentoInvalido, ConvenioNaoCobre).
     */
    public Pagamento processarPagamento(Consulta consulta, String tipo, int parcelas)
            throws PagamentoInvalidoException, ConvenioNaoCobreException {

        double valorBase = consulta.getProfissional().getValorConsulta();
        Pagamento pagamento;

        if (tipo.equalsIgnoreCase("dinheiro") || tipo.equalsIgnoreCase("pix")) {
            pagamento = new PagamentoDinheiro(valorBase, consulta);

        } else if (tipo.equalsIgnoreCase("cartao")) {
            // O proprio construtor valida o limite de parcelas.
            pagamento = new PagamentoCartao(valorBase, consulta, parcelas);

        } else if (tipo.equalsIgnoreCase("convenio")) {
            Convenio convenio = consulta.getPaciente().getConvenio();
            if (convenio == null) {
                throw new PagamentoInvalidoException("Paciente nao possui convenio cadastrado.");
            }
            String especialidade = consulta.getProfissional().getEspecialidade();
            // Verificacao de cobertura (cenario obrigatorio 12).
            if (!convenio.cobre(especialidade)) {
                throw new ConvenioNaoCobreException(
                        "Convenio " + convenio.getNome() + " nao cobre a especialidade " + especialidade + ".");
            }
            pagamento = new PagamentoConvenio(valorBase, consulta, convenio);

        } else {
            // Tipo nao reconhecido (ex.: cheque/voucher) -> cenario obrigatorio 10.
            throw new PagamentoInvalidoException("Tipo de pagamento nao reconhecido: " + tipo);
        }

        pagamentos.add(pagamento);
        return pagamento;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    // ================= RELATORIOS / EXPORTACAO =================

    // Lista unificada para o relatorio polimorfico (List<Pessoa>).
    public List<Pessoa> getTodasPessoas() {
        return todasPessoas;
    }

    /**
     * Reune todas as entidades Exportaveis (consultas, atendimentos, pagamentos)
     * em uma unica lista do tipo da interface, para exportacao uniforme.
     */
    public List<clinica.model.Exportavel> getExportaveis() {
        List<clinica.model.Exportavel> lista = new ArrayList<>();
        lista.addAll(consultas);
        lista.addAll(atendimentos);
        lista.addAll(pagamentos);
        return lista;
    }
}
